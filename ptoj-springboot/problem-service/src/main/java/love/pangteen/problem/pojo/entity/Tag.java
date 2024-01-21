package love.pangteen.problem.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/21 22:54
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Tag对象", description="")
public class Tag implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "标签名字")
    private String name;

    @ApiModelProperty(value = "标签颜色")
    private String color;

    @ApiModelProperty(value = "标签所属oj")
    private String oj;

    @ApiModelProperty(value = "团队ID")
    private Long gid;

    @ApiModelProperty(value = "标签分类ID")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long tcid;

    @TableField(fill = FieldFill.INSERT)
    private Date gmtCreate;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;
}
