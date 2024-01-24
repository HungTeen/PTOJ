package love.pangteen.user.pojo.vo;

import lombok.Data;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/24 16:17
 **/
@Data
public class CheckUsernameOrEmailVO {

    private Boolean email;

    private Boolean username;
}