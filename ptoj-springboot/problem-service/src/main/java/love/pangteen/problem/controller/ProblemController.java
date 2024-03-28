package love.pangteen.problem.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import love.pangteen.api.annotations.IgnoreLogin;
import love.pangteen.problem.manager.RecentProblemManager;
import love.pangteen.problem.pojo.dto.PidListDTO;
import love.pangteen.problem.pojo.entity.CodeTemplate;
import love.pangteen.problem.pojo.vo.ProblemInfoVO;
import love.pangteen.api.pojo.vo.ProblemVO;
import love.pangteen.problem.pojo.vo.RandomProblemVO;
import love.pangteen.problem.pojo.vo.RecentUpdatedProblemVO;
import love.pangteen.problem.service.CodeTemplateService;
import love.pangteen.problem.service.ProblemService;
import love.pangteen.result.CommonResult;
import org.hibernate.validator.constraints.Range;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/24 22:31
 **/
@RestController
@RequestMapping("/problem")
public class ProblemController {

    @Resource
    private ProblemService problemService;

    @Resource
    private CodeTemplateService codeTemplateService;

    @Resource
    private RecentProblemManager recentProblemManager;

    @RequestMapping(value = "/get-problem-list", method = RequestMethod.GET)
    @IgnoreLogin
    @Validated
    public CommonResult<Page<ProblemVO>> getProblemList(@Range(min = 1) @RequestParam(value = "limit", defaultValue = "1") Integer limit,
                                                        @Range(min = 1) @RequestParam(value = "currentPage", defaultValue = "10") Integer currentPage,
                                                        @NotBlank @RequestParam(value = "keyword", required = false) String keyword,
                                                        @RequestParam(value = "tagId", required = false) List<Long> tagId,
                                                        @RequestParam(value = "difficulty", required = false) Integer difficulty,
                                                        @RequestParam(value = "oj", required = false) String oj) {
        return CommonResult.success(problemService.getProblemList(limit, currentPage, keyword, tagId, difficulty, oj));
    }

    @GetMapping("/get-random-problem")
    @IgnoreLogin
    public CommonResult<RandomProblemVO> getRandomProblem() {
        return CommonResult.success(problemService.getRandomProblem());
    }

    @PostMapping("/get-user-problem-status")
    public CommonResult<HashMap<Long, Object>> getUserProblemStatus(@Validated @RequestBody PidListDTO pidListDto) {
        return CommonResult.success(problemService.getUserProblemStatus(pidListDto));
    }

    @RequestMapping(value = "/get-problem-detail", method = RequestMethod.GET)
    @IgnoreLogin
    public CommonResult<ProblemInfoVO> getProblemInfo(@RequestParam(value = "problemId", required = true) String problemId,
                                                      @RequestParam(value = "gid", required = false) Long gid) {
        return CommonResult.success(problemService.getProblemInfo(problemId, gid));
    }
//
//    @GetMapping("/get-last-ac-code")
//    public CommonResult<LastAcceptedCodeVO> getUserLastAcceptedCode(@RequestParam(value = "pid") Long pid,
//                                                                    @RequestParam(value = "cid", required = false) Long cid) {
//        return CommonResult.success(problemService.getUserLastAcceptedCode(pid, cid));
//    }
//
//    @GetMapping("/get-full-screen-problem-list")
//    public CommonResult<List<ProblemFullScreenListVO>> getFullScreenProblemList(@RequestParam(value = "tid", required = false) Long tid,
//                                                                                @RequestParam(value = "cid", required = false) Long cid) {
//        return CommonResult.success(problemService.getFullScreenProblemList(tid, cid));
//    }

    @GetMapping("/get-problem-code-template")
    @IgnoreLogin
    public CommonResult<List<CodeTemplate>> getProblemCodeTemplate(@RequestParam("pid") Long pid) {
        return CommonResult.success(codeTemplateService.getProblemCodeTemplate(pid));
    }

    @GetMapping("/get-recent-updated-problem")
    public CommonResult<List<RecentUpdatedProblemVO>> getRecentUpdatedProblemList() {
        return CommonResult.success(recentProblemManager.getRecentUpdatedProblemList());
    }

}
