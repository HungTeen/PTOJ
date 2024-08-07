package love.pangteen.api.pojo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/21 22:53
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value="Case对象", description="题目测试样例")
public class ProblemCase implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "题目id")
    private Long pid;

    @ApiModelProperty(value = "测试样例的输入")
    private String input;

    @ApiModelProperty(value = "测试样例的输出")
    private String output;

    @ApiModelProperty(value = "该测试样例的IO得分")
    private Integer score;

    @ApiModelProperty(value = "subtask分组的编号")
    private Integer groupNum;

    @ApiModelProperty(value = "0可用，1不可用")
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private Date gmtCreate;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;
}
