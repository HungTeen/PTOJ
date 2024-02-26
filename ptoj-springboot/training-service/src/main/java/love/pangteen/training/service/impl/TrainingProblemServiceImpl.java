package love.pangteen.training.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import love.pangteen.api.pojo.entity.Problem;
import love.pangteen.api.pojo.vo.ProblemVO;
import love.pangteen.api.service.IDubboProblemService;
import love.pangteen.exception.StatusFailException;
import love.pangteen.training.mapper.TrainingProblemMapper;
import love.pangteen.training.pojo.dto.TrainingProblemDTO;
import love.pangteen.training.pojo.entity.TrainingProblem;
import love.pangteen.training.pojo.vo.TrainingProblemListVO;
import love.pangteen.training.pojo.vo.TrainingVO;
import love.pangteen.training.service.TrainingProblemService;
import love.pangteen.training.service.TrainingService;
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

    @Override
    public IPage<TrainingVO> getTrainingDetailList(Integer limit, Integer currentPage, String keyword, Long categoryId, String auth) {
        return null;
    }

    @Override
    public TrainingVO getTrainingDetail(Long tid) {
        return null;
    }

    @Override
    public List<ProblemVO> getTrainingProblemList(Long tid) {
        return null;
    }
}
