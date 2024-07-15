package love.pangteen.problem.controller;

import love.pangteen.api.pojo.entity.Problem;
import love.pangteen.api.pojo.entity.ProblemCase;
import love.pangteen.problem.manager.RecentProblemManager;
import love.pangteen.problem.pojo.dto.ProblemDTO;
import love.pangteen.api.pojo.entity.Language;
import love.pangteen.problem.pojo.vo.RecentUpdatedProblemVO;
import love.pangteen.problem.service.LanguageService;
import love.pangteen.problem.service.ProblemService;
import love.pangteen.problem.utils.LanguageUtils;
import love.pangteen.result.CommonResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/7/10 23:04
 **/
@RestController
@RequestMapping("/test/problem")
public class ProblemTestController {

    @Resource
    private ProblemService problemService;

    @Resource
    private LanguageService languageService;

    @Resource
    private RecentProblemManager recentProblemManager;

    /**
     * 随机伪造题目数据。
     */
    @PostMapping("/mock-problem")
    public CommonResult mockProblem(@RequestParam Integer st, @RequestParam Integer ed) {
        List<Language> languages = languageService.getLanguages(LanguageUtils.ORIGIN_LANGUAGE, false);
        List<Language> dummyLanguages = List.of(languages.get(0));
        for(int i = st; i <= ed; ++ i){
            String problemId = "DUMMY_" + String.format("%04d", i);
            Problem problem = Problem.builder()
                    .problemId(problemId)
                    .title("Title " + i)
                    .author("Unknown")
                    .type(0)
                    .judgeMode("default")
                    .judgeCaseMode("default")
                    .timeLimit(1000)
                    .memoryLimit(256)
                    .stackLimit(128)
                    .description("Nothing")
                    .input(String.valueOf(i))
                    .output(String.valueOf(i))
                    .examples("")
                    .isRemote(false)
                    .difficulty(0)
                    .auth(1)
                    .codeShare(true)
                    .isRemoveEndBlank(true)
                    .caseVersion("666")
                    .isGroup(false)
                    .isFileIO(false)
                    .build();
            ProblemDTO problemDTO = ProblemDTO.builder()
                    .problem(problem)
                    .samples(List.of(
                            ProblemCase.builder()
                                    .input(String.valueOf(i))
                                    .output(String.valueOf(i))
                                    .status(0)
                                    .groupNum(1)
                                    .build()
                    ))
                    .languages(dummyLanguages)
                    .isUploadTestCase(false)
                    .judgeMode("default")
                    .changeModeCode(false)
                    .changeJudgeCaseMode(false)
                    .build();
            problemService.addProblem(problemDTO);
        }
        return CommonResult.success();
    }

    @GetMapping("/recent-problems")
    public CommonResult<List<RecentUpdatedProblemVO>> getRecentUpdatedProblemList() {
        return CommonResult.success(recentProblemManager.getRecentUpdatedProblemList());
    }

}
