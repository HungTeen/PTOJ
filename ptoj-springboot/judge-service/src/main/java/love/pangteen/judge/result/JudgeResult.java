package love.pangteen.judge.result;

import lombok.Builder;
import lombok.Data;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/30 16:56
 **/
@Data
@Builder
public class JudgeResult {

    private Integer code;
    private String errMsg;
    private int time;
    private int memory;
}
