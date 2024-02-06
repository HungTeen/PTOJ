package love.pangteen.submission.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/26 22:10
 **/
@Configuration
public class RabbitMQConfig {

//    @Resource
//    private RabbitTemplate rabbitTemplate;
//
//    @PostConstruct
//    public void init(){
//        rabbitTemplate.setReturnsCallback(returnedMessage -> {
//
//        });
//    }

    @Bean
    public MessageConverter messageConverter(){
        // 1.定义消息转换器。
        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
        // 2.配置自动创建消息id，用于识别不同消息，也可以在业务中基于ID判断是否是重复消息。
        jackson2JsonMessageConverter.setCreateMessageIds(true);
        return jackson2JsonMessageConverter;
    }

}
