package love.pangteen.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/23 15:23
 **/
@Data
public class AccountProfile implements Serializable {

    private String uid;

    private String username;

    private String nickname;

    private String realname;

    private String titleName;

    private String titleColor;

    private String avatar;

    private int status;
}
