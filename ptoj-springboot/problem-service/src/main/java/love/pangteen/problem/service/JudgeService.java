package love.pangteen.problem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import love.pangteen.api.enums.JudgeStatus;
import love.pangteen.api.pojo.entity.Judge;
import love.pangteen.api.pojo.vo.ProblemCountVO;
import love.pangteen.api.pojo.vo.ProblemVO;
import love.pangteen.problem.pojo.dto.PidListDTO;
import love.pangteen.problem.pojo.dto.SubmitIdListDTO;
import love.pangteen.problem.pojo.vo.JudgeCaseVO;
import love.pangteen.problem.pojo.vo.JudgeVO;
import love.pangteen.problem.pojo.vo.TestJudgeVO;

import java.util.HashMap;
import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/25 16:10
 **/
public interface JudgeService extends IService<Judge> {

    Page<ProblemVO> getProblemList(Integer limit, Integer currentPage, String keyword, List<Long> tagIds, Integer difficulty, String oj);

    HashMap<Long, Object> getUserProblemStatus(PidListDTO pidListDto);

    IPage<JudgeVO> getSubmissionList(Integer limit, Integer currentPage, Boolean onlyMine, String searchPid, Integer searchStatus, String searchUsername, Boolean completeProblemID, Long gid);

    TestJudgeVO getTestJudgeResult(String testJudgeKey);

    HashMap<Long, Object> checkCommonJudgeResult(SubmitIdListDTO submitIdListDto);

    HashMap<Long, Object> checkContestJudgeResult(SubmitIdListDTO submitIdListDto);

    JudgeCaseVO getALLCaseResult(Long submitId);

    List<Judge> getSubmitJudges(List<Long> pidList, String uid, Long cid, Long gid);

    List<ProblemCountVO> getProblemListCount(List<Long> pidList);

    void updateStatus(JudgeStatus judgeStatus);
}
