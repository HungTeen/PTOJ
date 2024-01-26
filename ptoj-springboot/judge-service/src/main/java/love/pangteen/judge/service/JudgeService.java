package love.pangteen.judge.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import love.pangteen.judge.pojo.dto.SubmitIdListDTO;
import love.pangteen.judge.pojo.dto.ToJudgeDTO;
import love.pangteen.judge.pojo.entity.Judge;
import love.pangteen.judge.pojo.vo.JudgeCaseVO;
import love.pangteen.judge.pojo.vo.JudgeVO;
import love.pangteen.judge.pojo.vo.TestJudgeVO;

import java.util.HashMap;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/25 16:10
 **/
public interface JudgeService extends MPJBaseService<Judge> {
    void judge(Judge judge);

    void remoteJudge(ToJudgeDTO toJudgeDTO);

    IPage<JudgeVO> getJudgeList(Integer limit, Integer currentPage, Boolean onlyMine, String searchPid, Integer searchStatus, String searchUsername, Boolean completeProblemID, Long gid);

    TestJudgeVO getTestJudgeResult(String testJudgeKey);

    HashMap<Long, Object> checkCommonJudgeResult(SubmitIdListDTO submitIdListDto);

    HashMap<Long, Object> checkContestJudgeResult(SubmitIdListDTO submitIdListDto);

    JudgeCaseVO getALLCaseResult(Long submitId);
}
