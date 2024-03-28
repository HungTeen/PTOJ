package love.pangteen.user.pojo.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/3/26 22:32
 **/
@ApiModel(value = "公告数据", description = "")
@Data
@Builder
public class AnnouncementVO {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "通知标题")
    private String title;

    @ApiModelProperty(value = "通知内容")
    private String content;

    @ApiModelProperty(value = "发布者（必须为比赛创建者或者超级管理员才能）")
    private String uid;

    @ApiModelProperty(value = "发布者的用户名")
    private String username;

    @ApiModelProperty(value = "0可见，1不可见")
    private int status;

    @TableField(fill = FieldFill.INSERT)
    private Date gmtCreate;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;
}