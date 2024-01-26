package love.pangteen.submission;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/12 22:15
 **/
@EnableAsync
@EnableDubbo
@EnableTransactionManagement
@MapperScan("love.pangteen.submission.mapper")
@SpringBootApplication(scanBasePackages = "love.pangteen")
public class SubmissionApplication {

    public static void main(String[] args) {
        SpringApplication.run(SubmissionApplication.class, args);
    }

}
