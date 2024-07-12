package love.pangteen.judge.consumer;

import lombok.extern.slf4j.Slf4j;
import love.pangteen.api.constant.MQConstants;
import love.pangteen.api.enums.JudgeStatus;
import love.pangteen.api.message.JudgeMessage;
import love.pangteen.api.pojo.dto.TestJudgeDTO;
import love.pangteen.api.pojo.dto.ToJudgeDTO;
import love.pangteen.api.pojo.entity.Judge;
import love.pangteen.api.pojo.entity.Problem;
import love.pangteen.api.pojo.entity.TestJudgeContext;
import love.pangteen.api.service.IDubboProblemService;
import love.pangteen.judge.manager.JudgeManager;
import love.pangteen.judge.service.JudgeService;
import love.pangteen.manager.TestJudgeContentManager;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/3/15 17:23
 **/
@Slf4j
@RocketMQMessageListener(
        topic = MQConstants.JUDGE_TOPIC,
        consumerGroup = MQConstants.JUDGE_CONSUMER_GROUP
)
@Component
public class JudgeMessageConsumer implements RocketMQListener<JudgeMessage> {

    @Resource
    private JudgeService judgeService;

    @DubboReference
    private IDubboProblemService problemService;

    @Resource
    private JudgeManager judgeManager;

    @Resource
    private TestJudgeContentManager testJudgeContentManager;

    @Override
    public void onMessage(JudgeMessage msg) {
        if(msg.getIsLocalTest()){
            // 自测代码。
            Problem problem = problemService.getById(msg.getPid());
            if(problem != null){
                TestJudgeContext context = testJudgeContentManager.getTestJudgeContext(msg.getUniqueKey());
                TestJudgeDTO testJudgeDTO = new TestJudgeDTO();
                testJudgeDTO.setMemoryLimit(problem.getMemoryLimit())
                        .setTimeLimit(problem.getTimeLimit())
                        .setStackLimit(problem.getStackLimit())
                        .setCode(context.getCode())
                        .setLanguage(context.getLanguage())
                        .setUniqueKey(msg.getUniqueKey())
                        .setExpectedOutput(context.getExpectedOutput())
                        .setTestCaseInput(context.getUserInput())
                        .setProblemJudgeMode(problem.getJudgeMode())
                        .setIsRemoveEndBlank(problem.getIsRemoveEndBlank() || problem.getIsRemote())
                        .setIsFileIO(problem.getIsFileIO())
                        .setIoReadFileName(problem.getIoReadFileName())
                        .setIoWriteFileName(problem.getIoWriteFileName());
                judgeManager.submitProblemTestJudge(testJudgeDTO);
            }
        } else {
            Judge judge = judgeService.getById(msg.getJudgeId());
            if (judge != null) {
                if(JudgeStatus.STATUS_PENDING.getStatus().equals(judge.getStatus())){
                    judgeService.updateJudgeStatus(JudgeStatus.STATUS_SUBMITTING);
                    // 调用判题服务。
                    judgeManager.submitProblemJudge(new ToJudgeDTO()
                            .setJudge(judge)
                            .setRemoteJudgeProblem(null)
                    );
                } else if(JudgeStatus.STATUS_CANCELLED.getStatus().equals(judge.getStatus())){
                    // 调度评测时发现该评测任务被取消，则结束评测。
//                if (JudgeUtils.isContestSubmission(judge.getCid())) {
//                    UpdateWrapper<ContestRecord> updateWrapper = new UpdateWrapper<>();
//                    // 取消评测，不罚时也不算得分
//                    updateWrapper.set("status", Constants.Contest.RECORD_NOT_AC_NOT_PENALTY.getCode());
//                    updateWrapper.eq("submit_id", judge.getSubmitId()); // submit_id一定只有一个
//                    contestRecordEntityService.update(updateWrapper);
//                }
                } else if(JudgeStatus.STATUS_SUBMITTING.getStatus().equals(judge.getStatus())){
                    log.warn("评测机重复提交评测任务！评测ID：{}", judge.getSubmitId());
                } else {
                    log.warn("已跳过未知状态的评测任务！评测ID：{}", judge.getSubmitId());
                }
            }
        }
    }
}
