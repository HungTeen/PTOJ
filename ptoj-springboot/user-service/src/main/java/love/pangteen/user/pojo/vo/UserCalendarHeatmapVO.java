package love.pangteen.user.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/24 16:24
 **/
@Data
@ApiModel(value="用户主页的提交热力图数据类UserCalendarHeatmapVo", description="")
public class UserCalendarHeatmapVO implements Serializable {

    @ApiModelProperty(value = "结尾日期 例如 2022-02-02")
    private String endDate;

    @ApiModelProperty(value = "日期对应的提交次数数据列表")
    private List<HashMap<String,Object>> dataList;
}