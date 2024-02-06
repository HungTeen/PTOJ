package love.pangteen.judge.listener;

import love.pangteen.api.constant.MQConstants;
import love.pangteen.api.enums.JudgeStatus;
import love.pangteen.api.message.SubmissionMessage;
import love.pangteen.api.pojo.dto.ToJudgeDTO;
import love.pangteen.api.pojo.entity.Judge;
import love.pangteen.judge.manager.JudgeManager;
import love.pangteen.judge.service.JudgeService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/30 9:45
 **/
@Component
public class SubmissionListener {

    @Resource
    private JudgeService judgeService;

    @Resource
    private JudgeManager judgeManager;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MQConstants.JUDGE_WAITING_QUEUE, durable = "true"),
            exchange = @Exchange(value = MQConstants.JUDGE_EXCHANGE, type = ExchangeTypes.DIRECT, durable = "true"),
            key = MQConstants.GENERAL
    ))
    public void listenGeneralJudgeMsg(SubmissionMessage msg){
        Judge judge = judgeService.getById(msg.getJudgeId());
        if (judge != null) {
            // 调度评测时发现该评测任务被取消，则结束评测。
            if (Objects.equals(judge.getStatus(), JudgeStatus.STATUS_CANCELLED.getStatus())) {
//                if (JudgeUtils.isContestSubmission(judge.getCid())) {
//                    UpdateWrapper<ContestRecord> updateWrapper = new UpdateWrapper<>();
//                    // 取消评测，不罚时也不算得分
//                    updateWrapper.set("status", Constants.Contest.RECORD_NOT_AC_NOT_PENALTY.getCode());
//                    updateWrapper.eq("submit_id", judge.getSubmitId()); // submit_id一定只有一个
//                    contestRecordEntityService.update(updateWrapper);
//                }
            } else {
                // 调用判题服务。
                judgeManager.submitProblemJudge(new ToJudgeDTO()
                        .setJudge(judge)
                        .setRemoteJudgeProblem(null)
                );
            }
        }

    }

}
