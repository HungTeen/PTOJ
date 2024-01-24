package love.pangteen.problem.pojo.vo;

import lombok.Data;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/24 22:44
 **/
@Data
public class ProblemFullScreenListVO {

    private Long pid;

    private String problemId;

    private String title;

    private Integer status;

    private Integer score;
}
