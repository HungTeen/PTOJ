package love.pangteen.training.pojo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/2/26 10:24
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "Training对象", description = "训练题单实体")
public class Training implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "训练题单标题")
    @Length(max = 500, message = "标题长度不能超过500")
    private String title;

    @ApiModelProperty(value = "训练题单简介")
    @Length(max = 65535, message = "简介长度不能超过65535")
    private String description;

    @ApiModelProperty(value = "训练题单创建者用户名")
    private String author;

    @ApiModelProperty(value = "训练题单权限类型：Public、Private")
    @Pattern(regexp = "^(Public|Private)$", message = "权限类型只能为Public或Private")
    private String auth;

    @ApiModelProperty(value = "训练题单权限为Private时的密码")
    private String privatePwd;

    @ApiModelProperty(value = "是否可用")
    private Boolean status;

    @ApiModelProperty(value = "编号，升序排序")
    @TableField("`rank`")
    private Integer rank;

    @ApiModelProperty(value = "是否为团队内的训练")
    private Boolean isGroup;

    @ApiModelProperty(value = "团队ID")
    private Long gid;

    @TableField(fill = FieldFill.INSERT)
    private Date gmtCreate;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;
}
