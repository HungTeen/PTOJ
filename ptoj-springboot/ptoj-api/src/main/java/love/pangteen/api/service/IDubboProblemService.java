package love.pangteen.api.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import love.pangteen.api.pojo.entity.Problem;
import love.pangteen.api.pojo.entity.ProblemCase;

import java.util.List;
import java.util.Map;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/26 10:15
 **/
public interface IDubboProblemService {

    Map<Long, String> getProblemTitleMap(List<Long> pidList);

    Problem getById(Long pid);

    Problem getByProblemId(String problemId);

    List<ProblemCase> getProblemCases(Long pid);

    boolean removeById(Long pid);

    IPage<Problem> getTrainingProblemPage(Integer limit, Integer currentPage, String keyword, Boolean queryExisted, List<Long> pidList);
}
