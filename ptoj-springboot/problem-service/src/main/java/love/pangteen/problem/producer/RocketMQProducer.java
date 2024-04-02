package love.pangteen.problem.producer;

import lombok.extern.slf4j.Slf4j;
import love.pangteen.api.constant.MQConstants;
import love.pangteen.api.message.JudgeMessage;
import love.pangteen.api.message.SubmitMessage;
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

    public void sendJudgeTask(JudgeMessage message) {
        rocketMQTemplate.send(MQConstants.JUDGE_TOPIC, new GenericMessage<>(message));
    }

    public void sendSubmitTask(SubmitMessage message){
        rocketMQTemplate.send(MQConstants.SUBMIT_TOPIC, new GenericMessage<>(message));
    }
}
