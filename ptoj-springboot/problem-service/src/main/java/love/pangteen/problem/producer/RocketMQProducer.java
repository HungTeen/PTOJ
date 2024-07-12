package love.pangteen.problem.producer;

import lombok.extern.slf4j.Slf4j;
import love.pangteen.api.constant.MQConstants;
import love.pangteen.api.enums.JudgeStatus;
import love.pangteen.api.message.JudgeMessage;
import love.pangteen.api.message.SubmitMessage;
import love.pangteen.problem.service.JudgeService;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/3/15 16:53
 **/
@Slf4j
@Component
public class RocketMQProducer {

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Resource
    private JudgeService judgeService;

    /**
     * 异步发送判题请求，消息重复通过JudgeID判断。
     */
    public void sendJudgeTask(JudgeMessage message) {
        rocketMQTemplate.asyncSend(MQConstants.JUDGE_TOPIC, new GenericMessage<>(message), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("判题消息发送成功！");
            }

            @Override
            public void onException(Throwable throwable) {
                judgeService.updateStatus(JudgeStatus.STATUS_SUBMITTED_FAILED);
                log.error("判题消息发送失败！");
            }
        });
    }

    /**
     * 暂时没用，并且消息会重复。
     */
    public void sendSubmitTask(SubmitMessage message){
//        rocketMQTemplate.asyncSend(MQConstants.SUBMIT_TOPIC, new GenericMessage<>(message), new SendCallback() {
//            @Override
//            public void onSuccess(SendResult sendResult) {
//
//            }
//
//            @Override
//            public void onException(Throwable e) {
//
//            }
//        });
    }
}
