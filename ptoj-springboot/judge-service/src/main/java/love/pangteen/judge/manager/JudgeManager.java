package love.pangteen.judge.manager;

import cn.hutool.core.util.StrUtil;
import love.pangteen.api.enums.JudgeStatus;
import love.pangteen.api.message.AcceptMessage;
import love.pangteen.api.pojo.dto.TestJudgeDTO;
import love.pangteen.api.pojo.dto.ToJudgeDTO;
import love.pangteen.api.pojo.entity.Judge;
import love.pangteen.api.pojo.entity.TestJudgeResult;
import love.pangteen.judge.producer.RocketMQProducer;
import love.pangteen.judge.service.JudgeService;
import love.pangteen.manager.TestJudgeContentManager;
import love.pangteen.result.CommonResult;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/30 11:11
 **/
@Component
public class JudgeManager {

    @Resource
    private JudgeService judgeService;

    @Resource
    private TestJudgeContentManager testJudgeContentManager;

    @Resource
    private RocketMQProducer rocketMQProducer;

    public CommonResult<Void> submitProblemJudge(ToJudgeDTO toJudgeDTO) {
        Judge judge = toJudgeDTO.getJudge();

        if (judge == null || judge.getSubmitId() == null || judge.getUid() == null || judge.getPid() == null) {
            return CommonResult.clientError("调用参数错误！请检查您的调用参数！");
        }

        JudgeStatus status = judgeService.judge(judge);
        if(status == JudgeStatus.STATUS_ACCEPTED){
            long acCount = judgeService.getUserAcceptCount(judge.getUid());
            rocketMQProducer.postAcceptMessage(new AcceptMessage(judge.getUid(), judge.getPid(), acCount));
        }

        return CommonResult.successMsg("判题机评测完成！");
    }

    public CommonResult<Void> submitProblemTestJudge(TestJudgeDTO testJudgeDTO) {
        if (testJudgeDTO == null
                || StrUtil.isEmpty(testJudgeDTO.getCode())
                || StrUtil.isEmpty(testJudgeDTO.getLanguage())
                || StrUtil.isEmpty(testJudgeDTO.getUniqueKey())
                || testJudgeDTO.getTimeLimit() == null
                || testJudgeDTO.getMemoryLimit() == null
                || testJudgeDTO.getStackLimit() == null) {
            if(testJudgeDTO != null && testJudgeDTO.getUniqueKey() != null){
                TestJudgeResult result = TestJudgeResult.builder()
                        .status(JudgeStatus.STATUS_SYSTEM_ERROR.getStatus())
                        .time(0L)
                        .memory(0L)
                        .stderr("调用参数错误！请检查您的调用参数！")
                        .build();
                testJudgeContentManager.saveTestJudgeResult(testJudgeDTO.getUniqueKey(), result);
            }
            return CommonResult.clientError("调用参数错误！请检查您的调用参数！");
        }

        TestJudgeResult result = judgeService.testJudge(testJudgeDTO);
        result.setInput(testJudgeDTO.getTestCaseInput());
        result.setExpectedOutput(testJudgeDTO.getExpectedOutput());
        result.setProblemJudgeMode(testJudgeDTO.getProblemJudgeMode());
        testJudgeContentManager.saveTestJudgeResult(testJudgeDTO.getUniqueKey(), result);

        return CommonResult.successMsg("自测成功");
    }

}
