package love.pangteen.user.pojo.dto;

import lombok.Data;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/24 16:25
 **/
@Data
public class ChangePasswordDTO {

    private String oldPassword;

    private String newPassword;
}