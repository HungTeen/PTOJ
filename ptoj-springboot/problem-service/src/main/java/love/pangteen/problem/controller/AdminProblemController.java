package love.pangteen.problem.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/21 22:25
 **/
@RestController

@RequestMapping("/admin/problem")
public class AdminProblemController {

//    @Resource
//    private AdminProblemService adminProblemService;
//
//    @GetMapping("/get-problem-list")
//    public CommonResult<IPage<Problem>> getProblemList(@RequestParam(value = "limit", required = false) Integer limit,
//                                                       @RequestParam(value = "currentPage", required = false) Integer currentPage,
//                                                       @RequestParam(value = "keyword", required = false) String keyword,
//                                                       @RequestParam(value = "auth", required = false) Integer auth,
//                                                       @RequestParam(value = "oj", required = false) String oj) {
//        return adminProblemService.getProblemList(limit, currentPage, keyword, auth, oj);
//    }
//
//    @GetMapping("")
//    public CommonResult<Problem> getProblem(@RequestParam("pid") Long pid) {
//        return adminProblemService.getProblem(pid);
//    }
//
//    @DeleteMapping("")
//    public CommonResult<Void> deleteProblem(@RequestParam("pid") Long pid) {
//        return adminProblemService.deleteProblem(pid);
//    }
//
//    @PostMapping("")
//    public CommonResult<Void> addProblem(@RequestBody ProblemDTO problemDto) {
//        return adminProblemService.addProblem(problemDto);
//    }
//
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
