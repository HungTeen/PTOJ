package love.pangteen.problem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import love.pangteen.problem.pojo.dto.ProblemDTO;
import love.pangteen.problem.pojo.entity.Problem;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/22 8:48
 **/
public interface ProblemService extends IService<Problem> {
    IPage<Problem> getProblemList(Integer limit, Integer currentPage, String keyword, Integer auth, String oj);

    Problem getProblem(Long pid);

    void deleteProblem(Long pid);

    void addProblem(ProblemDTO problemDto);
}
