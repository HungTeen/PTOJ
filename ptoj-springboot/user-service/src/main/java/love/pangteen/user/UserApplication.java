package love.pangteen.user;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/12 22:15
 **/
@EnableDubbo
@MapperScan("love.pangteen.user.mapper")
@SpringBootApplication(scanBasePackages = "love.pangteen")
public class UserApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }

}