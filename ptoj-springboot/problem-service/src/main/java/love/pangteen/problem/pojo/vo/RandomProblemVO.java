package love.pangteen.problem.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/24 22:40
 **/
@Data
public class RandomProblemVO {

    @ApiModelProperty(value = "题目id")
    private String problemId;
}
