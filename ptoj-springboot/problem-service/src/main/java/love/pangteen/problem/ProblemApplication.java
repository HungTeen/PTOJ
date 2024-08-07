package love.pangteen.problem;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/12 22:15
 **/
@EnableCaching
@EnableAsync
@EnableDubbo
@EnableTransactionManagement
@MapperScan("love.pangteen.problem.mapper")
@SpringBootApplication(scanBasePackages = "love.pangteen")
public class ProblemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProblemApplication.class, args);
    }

}
