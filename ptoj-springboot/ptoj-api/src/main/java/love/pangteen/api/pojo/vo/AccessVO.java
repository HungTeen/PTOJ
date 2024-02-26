package love.pangteen.api.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/2/26 15:14
 **/
@Data
public class AccessVO {

    @ApiModelProperty(value = "是否有进入比赛或训练的权限")
    private Boolean access;
}
