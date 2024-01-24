package love.pangteen.user.pojo.dto;

import lombok.Data;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/24 16:27
 **/
@Data
public class ChangeEmailDTO {

    private String password;

    private String newEmail;

    private String code;
}
