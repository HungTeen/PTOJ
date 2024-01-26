package love.pangteen.submission.config;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/26 22:10
 **/
@Configuration
public class RabbitMQConfig {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init(){
        rabbitTemplate.setReturnsCallback(returnedMessage -> {

        });
    }
}
