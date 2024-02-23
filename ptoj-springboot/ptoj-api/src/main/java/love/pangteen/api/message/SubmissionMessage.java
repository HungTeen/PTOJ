package love.pangteen.api.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/30 9:37
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionMessage {

    /**
     * 正式提交代码必须。
     */
    Long judgeId;

    /**
     * 自测代码必须。
     */
    Long pid;

    /**
     * 是否是自测代码。
     */
    Boolean isLocalTest;

    /**
     * 自测代码唯一标识。
     */
    String uniqueKey;

    /**
     * 代码。
     */
    private String code;

    /**
     * 代码语言。
     */
    private String language;

    /**
     * 用户输入。
     */
    private String userInput;

    /**
     * 预期输出。
     */
    private String expectedOutput;

}
