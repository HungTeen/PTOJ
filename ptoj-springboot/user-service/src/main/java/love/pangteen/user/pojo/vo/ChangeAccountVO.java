package love.pangteen.user.pojo.vo;

import lombok.Data;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/24 16:25
 **/
@Data
public class ChangeAccountVO {

    private Integer code;

    private String msg;

    private UserInfoVO userInfo;
}