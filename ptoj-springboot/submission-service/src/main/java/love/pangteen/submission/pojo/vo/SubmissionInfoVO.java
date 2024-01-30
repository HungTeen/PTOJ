package love.pangteen.submission.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import love.pangteen.api.pojo.entity.Judge;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/25 16:30
 **/
@Data
public class SubmissionInfoVO {

    @ApiModelProperty(value = "提交详情")
    private Judge submission;

    @ApiModelProperty(value = "提交者是否可以分享该代码")
    private Boolean codeShare;
}
