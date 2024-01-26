package love.pangteen.submission.service;

import love.pangteen.submission.pojo.dto.SubmitJudgeDTO;
import love.pangteen.submission.pojo.dto.TestJudgeDTO;
import love.pangteen.submission.pojo.entity.Judge;
import love.pangteen.submission.pojo.vo.SubmissionInfoVO;

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
