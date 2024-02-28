package love.pangteen.training.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Pair;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import love.pangteen.api.pojo.entity.Problem;
import love.pangteen.api.pojo.vo.ProblemVO;
import love.pangteen.api.service.IDubboJudgeService;
import love.pangteen.api.service.IDubboProblemService;
import love.pangteen.exception.StatusFailException;
import love.pangteen.pojo.AccountProfile;
import love.pangteen.training.TrainingValidateUtils;
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
import java.util.*;
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

    @DubboReference
    private IDubboJudgeService judgeService;

    @Resource
    private TrainingValidateUtils validateUtils;

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

        PageDTO<Problem> problemPage = problemService.getTrainingProblemPage(limit, currentPage, keyword, queryExisted, pidList);
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
        Map<Long, List<Long>> tidPidMap = new HashMap<>();
        Set<Long> pidSet = new HashSet<>(); // 记录所有查询到的训练包含的题目id。

        // 查询每个训练包含多少题目。
        trainingList.forEach(trainingVO -> {
            List<Long> validPidList = getValidPidList(trainingVO.getId());
            pidSet.addAll(validPidList);
            tidPidMap.put(trainingVO.getId(), validPidList);
            trainingVO.setProblemCount(validPidList.size());
        });

        // 当前用户有登录，且训练列表不为空，则查询用户对于每个训练的做题进度。
        if (StpUtil.isLogin() && !trainingList.isEmpty()) {
            AccountProfile profile = AccountUtils.getProfile();
            HashMap<Long, Boolean> statusMap = judgeService.getUserAcceptCount(profile.getUuid(), pidSet);
            //记录用户每个训练题单通过的题目数。
            trainingList.forEach(trainingVO -> {
                List<Long> pidList = tidPidMap.get(trainingVO.getId());
                long count = pidList.stream().filter(id -> statusMap.containsKey(id) && statusMap.get(id)).count();
                trainingVO.setAcCount((int) count);
            });
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

        List<Long> pidList = getValidPidList(tid);
        trainingVo.setProblemCount(pidList.size());

        if(StpUtil.isLogin() && !pidList.isEmpty()){
            AccountProfile profile = AccountUtils.getProfile();
            HashMap<Long, Boolean> statusMap = judgeService.getUserAcceptCount(profile.getUuid(), pidList);
            long count = pidList.stream().filter(id -> statusMap.containsKey(id) && statusMap.get(id)).count();
            trainingVo.setAcCount((int) count);
        }

        return trainingVo;
    }

    @Override
    public List<ProblemVO> getTrainingProblemList(Long tid) {
        Training training = trainingService.getById(tid);
        if (training == null || !training.getStatus()) {
            throw new StatusFailException("该训练不存在或不允许显示！");
        }
        validateUtils.validateTrainingAuth(training);

        List<Long> pidList = getPidList(tid, true);
        List<ProblemVO> trainingProblemList = problemService.getTrainingProblemList(pidList);

        trainingProblemList.forEach(problemVO -> {
            Pair<Integer, Integer> stats = judgeService.getAcStats(problemVO.getPid());
            problemVO.setTotal(stats.getKey());
            problemVO.setAc(stats.getValue());
        });

        return trainingProblemList;
    }

    public List<Long> getPidList(Long tid, boolean sort) {
        return lambdaQuery().select(TrainingProblem::getPid)
                .eq(TrainingProblem::getTid, tid)
                .orderByAsc(sort, TrainingProblem::getRank).list()
                .stream()
                .map(TrainingProblem::getPid)
                .collect(Collectors.toList());
    }

    public List<Long> getValidPidList(Long tid) {
        return problemService.getValidPidList(getPidList(tid, false));
    }

}
