package love.pangteen.judge.producer;

import love.pangteen.api.constant.MQConstants;
import love.pangteen.api.message.AcceptMessage;
import love.pangteen.api.message.JudgeMessage;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/3/27 13:36
 **/
@Component
public class RocketMQProducer {

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    public void postAcceptMessage(AcceptMessage message) {
        rocketMQTemplate.send(MQConstants.ACCEPT_TOPIC, new GenericMessage<>(message));
    }

}
