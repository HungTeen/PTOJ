package love.pangteen.submission.producer;

import love.pangteen.api.constant.MQConstants;
import love.pangteen.api.message.JudgeMessage;
import love.pangteen.api.utils.RandomUtils;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/2/23 15:23
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class PublisherTest {

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Test
    public void sendTask() {
        for(int i = 0; i < 5; ++ i){
            JudgeMessage message = new JudgeMessage();
            message.setJudgeId((long) i);

            int priority = RandomUtils.get().nextInt(5);
            rocketMQTemplate.send(MQConstants.JUDGE_TOPIC, new GenericMessage<>(message));
        }
    }

}
