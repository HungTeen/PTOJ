package love.pangteen.judge.result;

import lombok.Data;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/30 16:56
 **/
@Data
public class JudgeResult {

    private Integer code;
    private String errMsg;
    private int time;
    private int memory;
}
