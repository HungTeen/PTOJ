package love.pangteen.judge.pojo.entity;

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
    private int score;
    private int oiRankScore;
}
