package love.pangteen.user.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/3/25 22:07
 **/
@Data
@Builder
public class DashboardVO {

    @ApiModelProperty(value = "用户总数")
    private int userNum;

    @ApiModelProperty(value = "今日判题数")
    private int todayJudgeNum;

}
