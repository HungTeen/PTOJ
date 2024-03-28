package love.pangteen.user.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/3/26 22:45
 **/
@Data
public class SubmissionStatisticsVO {

    @ApiModelProperty(value = "最近七天日期格式 mm-dd,升序")
    private List<String> dateStrList;

    @ApiModelProperty(value = "最近七天每天AC数量")
    private List<Long> acCountList;

    @ApiModelProperty(value = "最近七天每天提交数量")
    private List<Long> totalCountList;
}