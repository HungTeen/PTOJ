package love.pangteen.user.consumer;

import love.pangteen.api.constant.MQConstants;
import love.pangteen.api.message.AcceptMessage;
import love.pangteen.api.message.JudgeMessage;
import love.pangteen.user.manager.UserAcceptManager;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/3/28 18:08
 **/
@RocketMQMessageListener(
        topic = MQConstants.ACCEPT_TOPIC,
        consumerGroup = MQConstants.ACCEPT_GROUP
)
@Component
public class AcceptMessageConsumer implements RocketMQListener<AcceptMessage> {

    @Resource
    private UserAcceptManager userAcceptManager;

    @Override
    public void onMessage(AcceptMessage acceptMessage) {
        userAcceptManager.updateUserCount(acceptMessage.getUid(), acceptMessage.getAcCount(), true);
    }

}
