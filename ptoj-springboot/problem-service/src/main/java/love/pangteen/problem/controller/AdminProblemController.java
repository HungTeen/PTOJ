package love.pangteen.problem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import love.pangteen.problem.pojo.dto.ProblemDTO;
import love.pangteen.problem.pojo.entity.Problem;
import love.pangteen.problem.service.ProblemService;
import love.pangteen.result.CommonResult;
import org.hibernate.validator.constraints.Range;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

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
        problemService.deleteProblem(pid);
        return CommonResult.success();
    }

    @PostMapping("")
    public CommonResult<Void> addProblem(@Validated @RequestBody ProblemDTO problemDto) {
        problemService.addProblem(problemDto);
        return CommonResult.success();
    }

//    @PutMapping("")
//    public CommonResult<Void> updateProblem(@RequestBody ProblemDTO problemDto) {
//        return adminProblemService.updateProblem(problemDto);
//    }
//
//    @GetMapping("/get-problem-cases")
//    public CommonResult<List<ProblemCase>> getProblemCases(@RequestParam("pid") Long pid,
//                                                           @RequestParam(value = "isUpload", defaultValue = "true") Boolean isUpload) {
//        return adminProblemService.getProblemCases(pid, isUpload);
//    }
//
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
//
//    @PutMapping("/change-problem-auth")
//    public CommonResult<Void> changeProblemAuth(@RequestBody Problem problem) {
//        return adminProblemService.changeProblemAuth(problem);
//    }

}
