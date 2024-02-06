package love.pangteen.problem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import love.pangteen.problem.pojo.dto.PidListDTO;
import love.pangteen.problem.pojo.dto.ProblemDTO;
import love.pangteen.api.pojo.entity.Problem;
import love.pangteen.problem.pojo.vo.*;

import java.util.HashMap;
import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/22 8:48
 **/
public interface ProblemService extends IService<Problem> {

    Page<ProblemVO> getProblemList(Integer limit, Integer currentPage, String keyword, List<Long> tagIds, Integer difficulty, String oj);

    IPage<Problem> getProblemList(Integer limit, Integer currentPage, String keyword, Integer auth, String oj);

    Problem getProblem(Long pid);

    void deleteProblem(Long pid);

    void addProblem(ProblemDTO problemDto);

    void updateProblem(ProblemDTO problemDto);

    void changeProblemAuth(Problem problem);

    RandomProblemVO getRandomProblem();

    HashMap<Long, Object> getUserProblemStatus(PidListDTO pidListDto);

    ProblemInfoVO getProblemInfo(String problemId, Long gid);

    LastAcceptedCodeVO getUserLastAcceptedCode(Long pid, Long cid);

    List<ProblemFullScreenListVO> getFullScreenProblemList(Long tid, Long cid);

    String getProblemOJ(Long pid);
}
