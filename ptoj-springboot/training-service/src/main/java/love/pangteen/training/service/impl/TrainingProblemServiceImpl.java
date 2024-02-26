package love.pangteen.training.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import love.pangteen.api.pojo.entity.Problem;
import love.pangteen.api.pojo.vo.ProblemVO;
import love.pangteen.api.service.IDubboProblemService;
import love.pangteen.exception.StatusFailException;
import love.pangteen.pojo.AccountProfile;
import love.pangteen.training.mapper.TrainingMapper;
import love.pangteen.training.mapper.TrainingProblemMapper;
import love.pangteen.training.pojo.dto.TrainingProblemDTO;
import love.pangteen.training.pojo.entity.Training;
import love.pangteen.training.pojo.entity.TrainingCategory;
import love.pangteen.training.pojo.entity.TrainingProblem;
import love.pangteen.training.pojo.vo.TrainingProblemListVO;
import love.pangteen.training.pojo.vo.TrainingVO;
import love.pangteen.training.service.MappingTrainingCategoryService;
import love.pangteen.training.service.TrainingProblemService;
import love.pangteen.training.service.TrainingService;
import love.pangteen.utils.AccountUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/2/26 10:36
 **/
@Service
public class TrainingProblemServiceImpl extends ServiceImpl<TrainingProblemMapper, TrainingProblem> implements TrainingProblemService {

    @Resource
    private TrainingService trainingService;

    @Resource
    private TrainingMapper trainingMapper;

    @Resource
    private MappingTrainingCategoryService mappingTrainingCategoryService;

    @DubboReference
    private IDubboProblemService problemService;

    @Transactional
    @Override
    public void addProblemFromPublic(TrainingProblemDTO trainingProblemDto) {
        if(lambdaQuery()
                .eq(TrainingProblem::getPid, trainingProblemDto.getPid())
                .and(wrapper -> wrapper
                        .eq(TrainingProblem::getTid, trainingProblemDto.getTid())
                        .or()
                        .eq(TrainingProblem::getDisplayId, trainingProblemDto.getDisplayId())
                ).oneOpt().isPresent()) {
            throw new StatusFailException("添加失败，该题目已添加或者题目的训练展示ID已存在！");
        }
        TrainingProblem trainingProblem = new TrainingProblem().setTid(trainingProblemDto.getTid())
                .setPid(trainingProblemDto.getPid())
                .setDisplayId(trainingProblemDto.getDisplayId());
        if(save(trainingProblem)){
            trainingService.modified(trainingProblemDto.getTid()); // 该训练已经被修改。

            // 异步地同步用户对该题目的提交数据
            // TODO adminTrainingRecordManager.syncAlreadyRegisterUserRecord(tid, pid, newTProblem.getId());
        } else {
            throw new StatusFailException("添加失败！");
        }
    }

    @Override
    public void updateProblem(TrainingProblem trainingProblem) {
        boolean isOk = saveOrUpdate(trainingProblem);

        if (!isOk) {
            throw new StatusFailException("修改失败！");
        }
    }

    /**
     * 训练id不为null，表示就是从比赛列表移除而已。
     */
    @Override
    public void deleteProblem(Long pid, Long tid) {
        boolean isOk = false;
        if (tid != null) {
            isOk = lambdaUpdate().eq(TrainingProblem::getPid, pid).eq(TrainingProblem::getTid, tid).remove();
        } else {
            isOk = problemService.removeById(pid); // 远程调用删除题目！
        }
        if (isOk) { // 删除成功
            if(tid != null){
                trainingService.modified(tid);
            }
        } else {
            throw new StatusFailException(tid != null ? "移除失败！" : "删除失败！");
        }
    }

    @Override
    public TrainingProblemListVO getProblemList(Integer limit, Integer currentPage, String keyword, Boolean queryExisted, Long tid) {
        List<TrainingProblem> trainingProblems = lambdaQuery()
                .eq(TrainingProblem::getTid, tid)
                .orderByAsc(TrainingProblem::getDisplayId).list();
        HashMap<Long, TrainingProblem> trainingProblemMap = new HashMap<>();
        List<Long> pidList = new ArrayList<>();
        trainingProblems.forEach(trainingProblem -> {
            trainingProblemMap.put(trainingProblem.getPid(), trainingProblem);
            pidList.add(trainingProblem.getPid());
        });

        IPage<Problem> problemPage = problemService.getTrainingProblemPage(limit, currentPage, keyword, queryExisted, pidList);
        if(queryExisted && ! pidList.isEmpty()) {
            List<Problem> sortProblemList = problemPage.getRecords()
                    .stream()
                    .sorted(Comparator.comparingInt(problem -> trainingProblemMap.get(problem.getId()).getRank()))
                    .collect(Collectors.toList());
            problemPage.setRecords(sortProblemList);
        }
        return new TrainingProblemListVO()
                .setProblemList(problemPage)
                .setTrainingProblemMap(trainingProblemMap);
    }

    /**
     * 获取训练题单列表，可根据关键词、类别、权限、类型过滤。
     */
    @Override
    public IPage<TrainingVO> getTrainingDetailList(Integer limit, Integer currentPage, String keyword, Long categoryId, String auth) {
        Page<TrainingVO> page = new Page<>(currentPage, limit);
        List<TrainingVO> trainingList = trainingMapper.getTrainingList(page, categoryId, auth, keyword);

        // 查询每个训练包含多少题目。
        trainingList.forEach(trainingVO -> {
            List<TrainingProblem> pidList = lambdaQuery().select(TrainingProblem::getPid)
                    .eq(TrainingProblem::getTid, trainingVO.getId()).list();
            trainingVO.setProblemCount(pidList.size());
        });

        // 当前用户有登录，且训练列表不为空，则查询用户对于每个训练的做题进度。
        if (StpUtil.isLogin() && !trainingList.isEmpty()) {
            AccountProfile profile = AccountUtils.getProfile();
            List<Long> tidList = trainingList.stream().map(TrainingVO::getId).collect(Collectors.toList());
            List<TrainingProblem> trainingProblemList = getTrainingListAcceptedCountByUid(tidList, profile.getUuid());

            //记录用户每个训练题单通过的题目数。
            HashMap<Long, Integer> tidMapCount = new HashMap<>(trainingList.size());
            for (TrainingProblem trainingProblem : trainingProblemList) {
                int count = tidMapCount.getOrDefault(trainingProblem.getTid(), 0);
                count++;
                tidMapCount.put(trainingProblem.getTid(), count);
            }

            for (TrainingVO trainingVo : trainingList) {
                Integer count = tidMapCount.getOrDefault(trainingVo.getId(), 0);
                trainingVo.setAcCount(count);
            }
        }

        page.setRecords(trainingList);
        return page;
    }

    @Override
    public TrainingVO getTrainingDetail(Long tid) {
        Training training = trainingService.getById(tid);
        if (training == null || !training.getStatus()) {
            throw new StatusFailException("该训练不存在或不允许显示！");
        }

        Long gid = training.getGid();
        if (training.getIsGroup()) {
//            if (!StpUtil.is && !groupValidator.isGroupMember(userRolesVo.getUid(), training.getGid())) {
//                throw new StatusForbiddenException("对不起，您无权限操作！");
//            }
        } else {
            gid = null;
        }

        TrainingVO trainingVo = BeanUtil.copyProperties(training, TrainingVO.class);
        TrainingCategory trainingCategory = mappingTrainingCategoryService.getTrainingCategory(tid);
        trainingVo.setCategoryName(trainingCategory.getName());
        trainingVo.setCategoryColor(trainingCategory.getColor());
//        List<Long> trainingProblemIdList = getTrainingProblemIdList(training.getId());
//        trainingVo.setProblemCount(trainingProblemIdList.size());
//        if (StpUtil.isLogin() && trainingValidator.isInTrainingOrAdmin(training, userRolesVo)) {
//            AccountProfile profile = AccountUtils.getProfile();
//            Integer count = getUserTrainingACProblemCount(profile.getUuid(), gid, trainingProblemIdList);
//            trainingVo.setAcCount(count);
//        } else {
//            trainingVo.setAcCount(0);
//        }

        return trainingVo;
    }

    @Override
    public List<ProblemVO> getTrainingProblemList(Long tid) {
        Training training = trainingService.getById(tid);
        if (training == null || !training.getStatus()) {
            throw new StatusFailException("该训练不存在或不允许显示！");
        }
//        trainingValidator.validateTrainingAuth(training); TODO 写不出来。
//        List<ProblemVO> trainingProblemList = trainingProblemMapper.getTrainingProblemList(tid);
//        return trainingProblemList.stream().filter(distinctByKey(ProblemVO::getPid)).collect(Collectors.toList());
        return List.of();
    }

    private List<TrainingProblem> getTrainingListAcceptedCountByUid(List<Long> tidList, String uuid) {
        //TODO RPC调用Judge。
        return List.of();
    }
}
