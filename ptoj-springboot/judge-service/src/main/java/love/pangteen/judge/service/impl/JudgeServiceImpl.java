package love.pangteen.judge.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import love.pangteen.judge.mapper.JudgeMapper;
import love.pangteen.judge.pojo.dto.SubmitIdListDTO;
import love.pangteen.judge.pojo.dto.SubmitJudgeDTO;
import love.pangteen.judge.pojo.dto.TestJudgeDTO;
import love.pangteen.judge.pojo.dto.ToJudgeDTO;
import love.pangteen.judge.pojo.entity.Judge;
import love.pangteen.judge.pojo.vo.JudgeCaseVO;
import love.pangteen.judge.pojo.vo.JudgeVO;
import love.pangteen.judge.pojo.vo.SubmissionInfoVO;
import love.pangteen.judge.pojo.vo.TestJudgeVO;
import love.pangteen.judge.service.JudgeService;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/25 16:11
 **/
@Service
public class JudgeServiceImpl extends ServiceImpl<JudgeMapper, Judge> implements JudgeService {


    /**
     * 标志该判题过程进入编译阶段。
     */
    @Override
    public void judge(Judge judge) {

    }

    @Override
    public void remoteJudge(ToJudgeDTO toJudgeDTO) {

    }

    @Override
    public IPage<JudgeVO> getJudgeList(Integer limit, Integer currentPage, Boolean onlyMine, String searchPid, Integer searchStatus, String searchUsername, Boolean completeProblemID, Long gid) {
        // 只查看当前用户的提交
        if (onlyMine) {

        }
        return null;
    }

    @Override
    public SubmissionInfoVO getSubmission(Long submitId) {
        return null;
    }

    @Override
    public Judge submitProblemJudge(SubmitJudgeDTO judgeDto) {
        return null;
    }

    @Override
    public String submitProblemTestJudge(TestJudgeDTO testJudgeDto) {
        return null;
    }

    @Override
    public TestJudgeVO getTestJudgeResult(String testJudgeKey) {
        return null;
    }

    @Override
    public Judge resubmit(Long submitId) {
        return null;
    }

    @Override
    public void updateSubmission(Judge judge) {

    }

    @Override
    public HashMap<Long, Object> checkCommonJudgeResult(SubmitIdListDTO submitIdListDto) {
        return null;
    }

    @Override
    public HashMap<Long, Object> checkContestJudgeResult(SubmitIdListDTO submitIdListDto) {
        return null;
    }

    @Override
    public JudgeCaseVO getALLCaseResult(Long submitId) {
        return null;
    }
}
