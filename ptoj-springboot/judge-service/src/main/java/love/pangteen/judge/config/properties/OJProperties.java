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

    private Integer maxTaskNum;

//    @Value("${remote-judge.open}")
//    private Boolean isOpenRemoteJudge;
//
//    @Value("${remote-judge.max-task-num}")
//    private Integer RemoteJudgeMaxTaskNum;

    /**
     * 判题机名称。
     */
    private String name;

}
