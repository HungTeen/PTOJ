package love.pangteen.training;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/2/26 10:13
 **/
@EnableAsync
@EnableDubbo
@EnableTransactionManagement
@MapperScan("love.pangteen.training.mapper")
@SpringBootApplication(scanBasePackages = "love.pangteen")
public class TrainingApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrainingApplication.class, args);
    }

}
