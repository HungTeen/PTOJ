package love.pangteen.judge.service;

import love.pangteen.judge.pojo.dto.SubmitJudgeDTO;
import love.pangteen.judge.pojo.dto.TestJudgeDTO;
import love.pangteen.judge.pojo.entity.Judge;
import love.pangteen.judge.pojo.vo.SubmissionInfoVO;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/26 15:34
 **/
public interface SubmitService {

    SubmissionInfoVO getSubmission(Long submitId);

    Judge submitProblemJudge(SubmitJudgeDTO judgeDto);

    String submitProblemTestJudge(TestJudgeDTO testJudgeDto);

    Judge resubmit(Long submitId);

    void updateSubmission(Judge judge);
}
