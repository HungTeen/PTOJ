package love.pangteen.submission.publisher;

import love.pangteen.api.message.SubmissionMessage;
import love.pangteen.api.utils.RandomUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;
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
    private RabbitTemplate rabbitTemplate;

    @Test
    public void sendTask() {
        for(int i = 0; i < 5; ++ i){
            SubmissionMessage message = new SubmissionMessage();
            message.setJudgeId((long) i);

            int priority = RandomUtils.get().nextInt(5);
            // 优先处理比赛的提交任务，其次处理普通提交的提交任务。
            rabbitTemplate.convertAndSend("direct.test.queue", message, msg -> {
                msg.getMessageProperties().setPriority(priority);
                return msg;
            });
        }
    }

}
