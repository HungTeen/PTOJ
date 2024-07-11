package love.pangteen.judge.controller;

import love.pangteen.judge.service.JudgeService;
import love.pangteen.result.CommonResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/25 15:18
 **/
@RestController
@RequestMapping("/test/judge")
public class JudgeTestController {

    @Resource
    private JudgeService judgeService;

    /**
     * 随机伪造判题数据。
     */
    @PostMapping("/mock-judge")
    public CommonResult mockJudge(@RequestParam Integer tries) {
        for (int i = 0; i < tries; ++i)
            judgeService.randomInsertJudge();
        return CommonResult.success();
    }

    @PostMapping("/validate")
    public CommonResult validate(){
        judgeService.validateAllJudges();
        return CommonResult.success();
    }

}
