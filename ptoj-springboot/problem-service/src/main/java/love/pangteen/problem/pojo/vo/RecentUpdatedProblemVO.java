package love.pangteen.problem.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/3/26 22:45
 **/
@Data
@NoArgsConstructor
public class RecentUpdatedProblemVO implements Serializable {

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "题目的自定义ID 例如（HOJ-1000）")
    private String problemId;

    @ApiModelProperty(value = "题目")
    private String title;

    @ApiModelProperty(value = "0为ACM,1为OI")
    private Integer type;

    @ApiModelProperty(value = "创建时间")
    private Date gmtCreate;

    @ApiModelProperty(value = "最近更新时间")
    private Date gmtModified;
}
