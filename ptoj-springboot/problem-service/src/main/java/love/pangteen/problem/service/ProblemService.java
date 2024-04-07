package love.pangteen.problem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import love.pangteen.api.pojo.entity.Problem;
import love.pangteen.problem.pojo.dto.ProblemDTO;
import love.pangteen.problem.pojo.vo.LastAcceptedCodeVO;
import love.pangteen.problem.pojo.vo.ProblemFullScreenListVO;
import love.pangteen.problem.pojo.vo.ProblemInfoVO;
import love.pangteen.problem.pojo.vo.RandomProblemVO;

import java.util.List;
import java.util.Map;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/22 8:48
 **/
public interface ProblemService extends IService<Problem> {

    IPage<Problem> getProblemList(Integer limit, Integer currentPage, String keyword, Integer auth, String oj);

    Problem getProblem(Long pid);

    boolean deleteProblem(Long pid);

    boolean addProblem(ProblemDTO problemDto);

    void updateProblem(ProblemDTO problemDto);

    void changeProblemAuth(Problem problem);

    RandomProblemVO getRandomProblem();

    ProblemInfoVO getProblemInfo(String problemId, Long gid);

    LastAcceptedCodeVO getUserLastAcceptedCode(Long pid, Long cid);

    List<ProblemFullScreenListVO> getFullScreenProblemList(Long tid, Long cid);

    String getProblemOJ(Long pid);

    List<Long> getProblemsByCreateDate();

    Map<Long, String> getProblemTitleMap(List<Long> pidList);

    Problem getByProblemId(String problemId);
}
