package love.pangteen.judge.controller;

import love.pangteen.judge.pojo.dto.ToJudgeDTO;
import love.pangteen.judge.pojo.entity.Judge;
import love.pangteen.judge.service.JudgeService;
import love.pangteen.result.CommonResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/25 14:57
 **/
@RestController
@RequestMapping("/judge")
public class OldJudgeController {

    @Resource
    private JudgeService judgeService;

    @PostMapping
    public CommonResult<Void> submitProblemJudge(@RequestBody ToJudgeDTO toJudgeDTO) {

//        if (!Objects.equals(toJudgeDTO.getToken(), judgeToken)) {
//            return CommonResult.error(ResultStatus.ACCESS_DENIED, "对不起！您使用的判题服务调用凭证不正确！访问受限！");
//        }

        Judge judge = toJudgeDTO.getJudge();

        if (judge == null || judge.getSubmitId() == null || judge.getUid() == null || judge.getPid() == null) {
            return CommonResult.clientError("调用参数错误！请检查您的调用参数！");
        }

        judgeService.judge(judge);

        return CommonResult.success("判题机评测完成！");
    }


//    @PostMapping(value = "/test-judge")
//    public CommonResult<TestJudgeRes> submitProblemTestJudge(@RequestBody TestJudgeReq testJudgeReq) {
//
//        if (testJudgeReq == null
//                || StringUtils.isEmpty(testJudgeReq.getCode())
//                || StringUtils.isEmpty(testJudgeReq.getLanguage())
//                || StringUtils.isEmpty(testJudgeReq.getUniqueKey())
//                || testJudgeReq.getTimeLimit() == null
//                || testJudgeReq.getMemoryLimit() == null
//                || testJudgeReq.getStackLimit() == null) {
//            return CommonResult.errorResponse("调用参数错误！请检查您的调用参数！");
//        }
//
//        if (!Objects.equals(testJudgeReq.getToken(), judgeToken)) {
//            return CommonResult.errorResponse("对不起！您使用的判题服务调用凭证不正确！访问受限！", ResultStatus.ACCESS_DENIED);
//        }
//        return CommonResult.successResponse(judgeService.testJudge(testJudgeReq));
//    }
//
//
//    @PostMapping(value = "/compile-spj")
//    public CommonResult<Void> compileSpj(@RequestBody CompileDTO compileDTO) {
//
//        if (!Objects.equals(compileDTO.getToken(), judgeToken)) {
//            return CommonResult.errorResponse("对不起！您使用的判题服务调用凭证不正确！访问受限！", ResultStatus.ACCESS_DENIED);
//        }
//
//        try {
//            judgeService.compileSpj(compileDTO.getCode(), compileDTO.getPid(), compileDTO.getLanguage(), compileDTO.getExtraFiles());
//            return CommonResult.successResponse(null, "编译成功！");
//        } catch (SystemError systemError) {
//            return CommonResult.errorResponse(systemError.getStderr(), ResultStatus.SYSTEM_ERROR);
//        }
//    }
//
//    @PostMapping(value = "/compile-interactive")
//    public CommonResult<Void> compileInteractive(@RequestBody CompileDTO compileDTO) {
//
//        if (!Objects.equals(compileDTO.getToken(), judgeToken)) {
//            return CommonResult.errorResponse("对不起！您使用的判题服务调用凭证不正确！访问受限！", ResultStatus.ACCESS_DENIED);
//        }
//
//        try {
//            judgeService.compileInteractive(compileDTO.getCode(), compileDTO.getPid(), compileDTO.getLanguage(), compileDTO.getExtraFiles());
//            return CommonResult.successResponse(null, "编译成功！");
//        } catch (SystemError systemError) {
//            return CommonResult.errorResponse(systemError.getStderr(), ResultStatus.SYSTEM_ERROR);
//        }
//    }

    @PostMapping(value = "/remote-judge")
    public CommonResult<Void> remoteJudge(@RequestBody ToJudgeDTO toJudgeDTO) {
//        if (!openRemoteJudge) {
//            return CommonResult.errorResponse("对不起！该判题服务器未开启远程虚拟判题功能！", ResultStatus.ACCESS_DENIED);
//        }

//        if (!Objects.equals(toJudgeDTO.getToken(), judgeToken)) {
//            return CommonResult.error(ResultStatus.ACCESS_DENIED, "对不起！您使用的判题服务调用凭证不正确！访问受限！");
//        }


        if (toJudgeDTO.getJudge() == null) {
            return CommonResult.clientError("请求参数不能为空！");
        }

        judgeService.remoteJudge(toJudgeDTO);

        return CommonResult.success("提交成功");
    }

}
