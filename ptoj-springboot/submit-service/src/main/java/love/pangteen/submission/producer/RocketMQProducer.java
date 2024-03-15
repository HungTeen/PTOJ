package love.pangteen.submission.producer;

import lombok.extern.slf4j.Slf4j;
import love.pangteen.api.constant.MQConstants;
import love.pangteen.api.message.SubmissionMessage;
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

    public void sendTask(SubmissionMessage message, boolean isContest) {
        // 优先处理比赛的提交任务，其次处理普通提交的提交任务。
        rocketMQTemplate.send(MQConstants.JUDGE_TOPIC, new GenericMessage<>(message));
    }

}
