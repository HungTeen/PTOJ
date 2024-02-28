package love.pangteen.user.pojo.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/24 16:25
 **/
@Data
public class ChangePasswordDTO {

    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;

    @Length(min = 6, max = 20, message = "密码长度必须在6-20之间")
    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}