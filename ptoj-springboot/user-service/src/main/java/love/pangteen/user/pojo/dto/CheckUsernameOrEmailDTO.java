package love.pangteen.user.pojo.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/24 16:17
 **/
@Data
public class CheckUsernameOrEmailDTO {

    @NotBlank(message = "邮箱不能为空")
    private String email;

    @NotBlank(message = "用户名不能为空")
    private String username;
}
