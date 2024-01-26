package love.pangteen.submission.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import love.pangteen.api.annotations.RequiresPermissions;
import love.pangteen.submission.pojo.dto.SubmitIdListDTO;
import love.pangteen.submission.pojo.dto.SubmitJudgeDTO;
import love.pangteen.submission.pojo.dto.TestJudgeDTO;
import love.pangteen.submission.pojo.entity.Judge;
import love.pangteen.submission.pojo.vo.JudgeCaseVO;
import love.pangteen.submission.pojo.vo.JudgeVO;
import love.pangteen.submission.pojo.vo.SubmissionInfoVO;
import love.pangteen.submission.pojo.vo.TestJudgeVO;
import love.pangteen.submission.service.JudgeService;
import love.pangteen.submission.service.SubmitService;
import love.pangteen.result.CommonResult;
import org.hibernate.validator.constraints.Range;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/25 16:22
 **/
@RestController
@RequestMapping("/judge")
public class JudgeController {

    @Resource
    private JudgeService judgeService;

    @Resource
    private SubmitService submitService;

    /**
     * 通用查询判题记录列表。
     */
    @GetMapping("/get-submission-list")
    @Validated
    public CommonResult<IPage<JudgeVO>> getJudgeList(@Range(min = 1) @RequestParam(value = "limit", defaultValue = "1") Integer limit,
                                                     @Range(min = 1) @RequestParam(value = "currentPage", defaultValue = "30") Integer currentPage,
                                                     @RequestParam(value = "onlyMine", required = false) Boolean onlyMine,
                                                     @RequestParam(value = "problemID", required = false) String searchPid,
                                                     @RequestParam(value = "status", required = false) Integer searchStatus,
                                                     @RequestParam(value = "username", required = false) String searchUsername,
                                                     @RequestParam(value = "completeProblemID", defaultValue = "false") Boolean completeProblemID,
                                                     @RequestParam(value = "gid", required = false) Long gid) {
        return CommonResult.success(judgeService.getJudgeList(limit, currentPage, onlyMine, searchPid, searchStatus, searchUsername, completeProblemID, gid));
    }

    /**
     * 获取单个提交记录的详情。
     */
    @GetMapping("/get-submission-detail")
    public CommonResult<SubmissionInfoVO> getSubmission(@RequestParam(value = "submitId") Long submitId) {
        return CommonResult.success(submitService.getSubmission(submitId));
    }

    /**
     * 核心方法，判题就此开始。
     */
    @RequiresPermissions("/submit")
    @RequestMapping(value = "/submit-problem-judge", method = RequestMethod.POST)
    public CommonResult<Judge> submitProblemJudge(@RequestBody SubmitJudgeDTO judgeDto) {
        return CommonResult.success(submitService.submitProblemJudge(judgeDto));
    }

    @RequiresPermissions("/submit")
    @RequestMapping(value = "/submit-problem-test-judge", method = RequestMethod.POST)
    public CommonResult<String> submitProblemTestJudge(@RequestBody TestJudgeDTO testJudgeDto) {
        return CommonResult.success(submitService.submitProblemTestJudge(testJudgeDto));
    }

    @GetMapping("/get-test-judge-result")
    public CommonResult<TestJudgeVO> getTestJudgeResult(@RequestParam("testJudgeKey") String testJudgeKey) {
        return CommonResult.success(judgeService.getTestJudgeResult(testJudgeKey));
    }

    /**
     * 调用判题服务器提交失败超过60s后，用户点击按钮重新提交判题进入的方法。
     */
    @GetMapping(value = "/resubmit")
    public CommonResult<Judge> resubmit(@RequestParam("submitId") Long submitId) {
        return CommonResult.success(submitService.resubmit(submitId));
    }

    /**
     * 修改单个提交详情的分享权限。
     */
    @PutMapping("/submission")
    public CommonResult<Void> updateSubmission(@RequestBody Judge judge) {
        submitService.updateSubmission(judge);
        return CommonResult.success();
    }

    /**
     * 对提交列表状态为Pending和Judging的提交进行更新检查。
     */
    @RequestMapping(value = "/check-submissions-status", method = RequestMethod.POST)
    public CommonResult<HashMap<Long, Object>> checkCommonJudgeResult(@RequestBody SubmitIdListDTO submitIdListDto) {
        return CommonResult.success(judgeService.checkCommonJudgeResult(submitIdListDto));
    }

    /**
     * 需要检查是否为封榜，是否可以查询结果，避免有人恶意查询。
     */
    @RequestMapping(value = "/check-contest-submissions-status", method = RequestMethod.POST)
    public CommonResult<HashMap<Long, Object>> checkContestJudgeResult(@RequestBody SubmitIdListDTO submitIdListDto) {
        return CommonResult.success(judgeService.checkContestJudgeResult(submitIdListDto));
    }


    /**
     * 获得指定提交id的测试样例结果，暂不支持查看测试数据，只可看测试点结果，时间，空间，或者IO得分。
     */
    @GetMapping("/get-all-case-result")
    public CommonResult<JudgeCaseVO> getALLCaseResult(@RequestParam(value = "submitId", required = true) Long submitId) {
        return CommonResult.success(judgeService.getALLCaseResult(submitId));
    }
}
