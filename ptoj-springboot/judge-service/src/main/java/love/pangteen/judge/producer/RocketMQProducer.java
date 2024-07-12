package love.pangteen.judge.producer;

import lombok.extern.slf4j.Slf4j;
import love.pangteen.api.constant.MQConstants;
import love.pangteen.api.message.AcceptMessage;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/3/27 13:36
 **/
@Slf4j
@Component
public class RocketMQProducer {

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 消息是幂等的，可以重复发送。
     */
    public void postAcceptMessage(AcceptMessage message) {
        rocketMQTemplate.asyncSend(MQConstants.ACCEPT_TOPIC, new GenericMessage<>(message), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("AC消息发送成功，消息ID：{}", sendResult.getMsgId());
            }

            @Override
            public void onException(Throwable e) {
                log.error("AC消息发送失败，消息：{}", message, e);
            }
        });
    }

}
