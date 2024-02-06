package love.pangteen.user.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/16 13:08
 **/
@Data
@ApiModel(description = "登录表单实体")
public class LoginDTO {

    @NotBlank(message = "用户名不能为空")
    @Length(max = 20, message = "用户名长度不能超过20")
    @ApiModelProperty(value = "用户名", required = true)
    private String username;

    @NotBlank(message = "密码不能为空")
    @Length(min = 6, max = 20, message = "密码长度必须在6-20之间")
    @ApiModelProperty(value = "密码", required = true)
    private String password;

}
