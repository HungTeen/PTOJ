package love.pangteen.user.pojo.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/21 13:51
 **/
@Data
public class AdminEditUserDTO {

    @NotBlank(message = "username不能为空")
    @Length(max = 20, message = "用户名长度建议不能超过20位!")
    private String username;

    @NotBlank(message = "uid不能为空")
    private String uid;

    @Length(max = 50, message = "真实姓名的长度不能超过50位")
    private String realname;

    private String email;

    @Length(min = 6, max = 20, message = "密码长度建议为6~20位！")
    private String password;

    private Integer type;

    private Integer status;

    private Boolean setNewPwd;

    @Length(max = 20, message = "头衔的长度建议不要超过20位")
    private String titleName;

    private String titleColor;
}
