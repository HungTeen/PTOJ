package love.pangteen.user.pojo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/17 19:58
 **/
@Data
public class EditUserInfoDTO {

    @NotNull(message = "用户id不能为空")
    @ApiModelProperty(value = "用户id")
    private String uid;

    @ApiModelProperty(value = "用户名")
    private String username;

    @Length(max = 20, message = "昵称长度过长")
    @ApiModelProperty(value = "昵称")
    private String nickname;

    @ApiModelProperty(value = "头像")
    private String avatar;

    @ApiModelProperty(value = "头衔名称")
    private String titleName;

    @ApiModelProperty(value = "头衔背景颜色")
    private String titleColor;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @Length(max = 200, message = "学号长度过长")
    @ApiModelProperty(value = "学号")
    private String number;

    @ApiModelProperty(value = "性别")
    private String gender;

    @Length(max = 100, message = "学校长度过长")
    @ApiModelProperty(value = "学校")
    private String school;

    @ApiModelProperty(value = "专业")
    private String course;

    @Length(max = 65535, message = "个性签名长度过长")
    @ApiModelProperty(value = "个性签名")
    private String signature;

    @Length(max = 50, message = "真实姓名长度过长")
    @ApiModelProperty(value = "真实姓名")
    private String realname;

    @Length(max = 255, message = "github地址长度过长")
    @ApiModelProperty(value = "github地址")
    private String github;

    @Length(max = 255, message = "博客地址长度过长")
    @ApiModelProperty(value = "博客地址")
    private String blog;

    @Length(max = 255, message = "cf用户名长度过长")
    @ApiModelProperty(value = "cf的username")
    private String cfUsername;

    @ApiModelProperty(value = "角色列表")
    private List<String> roleList;

}
