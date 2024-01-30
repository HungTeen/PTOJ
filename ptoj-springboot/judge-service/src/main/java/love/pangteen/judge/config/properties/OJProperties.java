package love.pangteen.judge.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/30 15:36
 **/
@Data
@Configuration
@ConfigurationProperties(prefix = "ptoj-judge-server")
public class OJProperties {

    /**
     * 判题机名称。
     */
    private String name;

}
