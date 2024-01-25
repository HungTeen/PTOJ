package love.pangteen.problem.controller;

import love.pangteen.api.annotations.IgnoreLogin;
import love.pangteen.problem.pojo.entity.Language;
import love.pangteen.problem.service.LanguageService;
import love.pangteen.problem.service.ProblemLanguageService;
import love.pangteen.result.CommonResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/24 23:00
 **/
@RestController
@RequestMapping("/language")
public class LanguageController {

    @Resource
    private LanguageService languageService;

    @Resource
    private ProblemLanguageService problemLanguageService;

    /**
     * 获取当前题目的oj的语言列表。
     */
    @GetMapping("/languages")
    @IgnoreLogin
    public CommonResult<List<Language>> getLanguages(@RequestParam(value = "pid", required = false) Long pid,
                                                     @RequestParam(value = "all", defaultValue = "false") Boolean all) {
        return CommonResult.success(languageService.getLanguages(pid, all));
    }

    /**
     * 获取当前题目的语言列表。
     */
    @GetMapping("/get-problem-languages")
    @IgnoreLogin
    public CommonResult<Collection<Language>> getProblemLanguages(@RequestParam("pid") Long pid) {
        return CommonResult.success(problemLanguageService.getProblemLanguages(pid));
    }
}
