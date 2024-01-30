package love.pangteen.judge.controller;

import love.pangteen.judge.pojo.vo.JudgeServerInfoVO;
import love.pangteen.judge.service.OJService;
import love.pangteen.result.CommonResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/25 15:18
 **/
@RestController
@RequestMapping("/judge-server")
public class JudgeServerController {

    @Resource
    private OJService judgeServerEntityService;

    @RequestMapping("/version")
    public CommonResult<JudgeServerInfoVO> getVersion() {
        //TODO /version 改成了 /judge-server/version，但是前端没找到发请求的地方。
        return CommonResult.success(judgeServerEntityService.getJudgeServerInfo(), "运行正常");
    }
}
