package love.pangteen.training.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import love.pangteen.api.pojo.vo.ProblemVO;
import love.pangteen.training.pojo.dto.TrainingProblemDTO;
import love.pangteen.training.pojo.entity.TrainingProblem;
import love.pangteen.training.pojo.vo.TrainingProblemListVO;
import love.pangteen.training.pojo.vo.TrainingVO;

import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/2/26 10:36
 **/
public interface TrainingProblemService extends IService<TrainingProblem> {

    void addProblemFromPublic(TrainingProblemDTO trainingProblemDto);

    void updateProblem(TrainingProblem trainingProblem);

    void deleteProblem(Long pid, Long tid);

    TrainingProblemListVO getProblemList(Integer limit, Integer currentPage, String keyword, Boolean queryExisted, Long tid);

    IPage<TrainingVO> getTrainingDetailList(Integer limit, Integer currentPage, String keyword, Long categoryId, String auth);

    TrainingVO getTrainingDetail(Long tid);

    List<ProblemVO> getTrainingProblemList(Long tid);

}
