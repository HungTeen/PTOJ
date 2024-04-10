package love.pangteen.api.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/4/9 23:39
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TestJudgeContext {

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
