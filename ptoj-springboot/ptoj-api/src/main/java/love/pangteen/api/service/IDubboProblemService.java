package love.pangteen.api.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import love.pangteen.api.pojo.entity.Language;
import love.pangteen.api.pojo.entity.Problem;
import love.pangteen.api.pojo.entity.ProblemCase;
import love.pangteen.api.pojo.vo.ProblemVO;

import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/26 10:15
 **/
public interface IDubboProblemService {

    Problem getById(Long pid);

    Problem getByProblemId(String problemId);

    List<ProblemCase> getProblemCases(Long pid);

    boolean removeById(Long pid);

    PageDTO<Problem> getTrainingProblemPage(Integer limit, Integer currentPage, String keyword, Boolean queryExisted, List<Long> pidList);

    /**
     * 获取有效的题目ID列表。
     */
    List<Long> getValidPidList(List<Long> pidList);

    List<ProblemVO> getTrainingProblemList(List<Long> pidList);

    List<Problem> getProblems(List<Long> acPidList);

    List<Problem> getAllProblems();

    List<Language> getAllLanguage();
}
