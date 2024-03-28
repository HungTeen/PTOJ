package love.pangteen.problem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import love.pangteen.api.interfaces.ValidateGroups;
import love.pangteen.problem.manager.RecentProblemManager;
import love.pangteen.problem.pojo.dto.ProblemDTO;
import love.pangteen.api.pojo.entity.Problem;
import love.pangteen.api.pojo.entity.ProblemCase;
import love.pangteen.problem.service.ProblemCaseService;
import love.pangteen.problem.service.ProblemService;
import love.pangteen.result.CommonResult;
import org.hibernate.validator.constraints.Range;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.groups.Default;
import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/21 22:25
 **/
@RestController

@RequestMapping("/admin/problem")
public class AdminProblemController {

    @Resource
    private ProblemService problemService;

    @Resource
    private ProblemCaseService problemCaseService;

    @Resource
    private RecentProblemManager recentProblemManager;

    @GetMapping("/get-problem-list")
    public CommonResult<IPage<Problem>> getProblemList(@Range(min = 1) @RequestParam(value = "limit", defaultValue = "10") Integer limit,
                                                       @Range(min = 1) @RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
                                                       @RequestParam(value = "keyword", required = false) String keyword,
                                                       @RequestParam(value = "auth", required = false) Integer auth,
                                                       @RequestParam(value = "oj", required = false) String oj) {
        return CommonResult.success(problemService.getProblemList(limit, currentPage, keyword, auth, oj));
    }

    @GetMapping("")
    public CommonResult<Problem> getProblem(@RequestParam("pid") Long pid) {
        return CommonResult.success(problemService.getProblem(pid));
    }

    @DeleteMapping("")
    public CommonResult<Void> deleteProblem(@RequestParam("pid") Long pid) {
        if(problemService.deleteProblem(pid)){
            recentProblemManager.removeProblem(pid);
        }
        return CommonResult.success();
    }

    @PostMapping("")
    public CommonResult<Void> addProblem(@Validated @RequestBody ProblemDTO problemDto) {
        if(problemService.addProblem(problemDto)){
            recentProblemManager.createProblem(problemDto.getProblem().getId());
        }
        return CommonResult.success();
    }

    @PutMapping("")
    public CommonResult<Void> updateProblem(@Validated({Default.class, ValidateGroups.Update.class}) @RequestBody ProblemDTO problemDto) {
        problemService.updateProblem(problemDto);
        return CommonResult.success();
    }

    @GetMapping("/get-problem-cases")
    public CommonResult<List<ProblemCase>> getProblemCases(@RequestParam("pid") Long pid, @RequestParam(value = "isUpload", defaultValue = "true") Boolean isUpload) {
        return CommonResult.success(problemCaseService.getProblemCases(pid, isUpload));
    }

//    @PostMapping("/compile-spj")
//    public CommonResult<Void> compileSpj(@RequestBody CompileDTO compileDTO) {
//        return adminProblemService.compileSpj(compileDTO);
//    }
//
//    @PostMapping("/compile-interactive")
//    public CommonResult<Void> compileInteractive(@RequestBody CompileDTO compileDTO) {
//        return adminProblemService.compileInteractive(compileDTO);
//    }
//
//    @GetMapping("/import-remote-oj-problem")
//    public CommonResult<Void> importRemoteOJProblem(@RequestParam("name") String name,
//                                                    @RequestParam("problemId") String problemId) {
//        return adminProblemService.importRemoteOJProblem(name, problemId);
//    }

    @PutMapping("/change-problem-auth")
    public CommonResult<Void> changeProblemAuth(@RequestBody Problem problem) {
        problemService.changeProblemAuth(problem);
        return CommonResult.success();
    }

}
