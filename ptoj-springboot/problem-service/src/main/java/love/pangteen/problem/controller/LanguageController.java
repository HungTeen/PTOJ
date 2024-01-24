package love.pangteen.problem.controller;

import love.pangteen.api.annotations.IgnoreLogin;
import love.pangteen.problem.pojo.entity.Language;
import love.pangteen.result.CommonResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/languages")
    @IgnoreLogin
    public CommonResult<List<Language>> getLanguages(@RequestParam(value = "pid", required = false) Long pid,
                                                     @RequestParam(value = "all", required = false) Boolean all) {
        return commonService.getLanguages(pid, all);
    }

    @GetMapping("/get-problem-languages")
    @IgnoreLogin
    public CommonResult<Collection<Language>> getProblemLanguages(@RequestParam("pid") Long pid) {
        return commonService.getProblemLanguages(pid);
    }
}
