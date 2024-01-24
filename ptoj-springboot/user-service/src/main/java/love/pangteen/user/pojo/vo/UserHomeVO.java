package love.pangteen.user.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/24 16:21
 **/
@ApiModel(value = "用户主页的数据格式类UserHomeVO", description = "")
@Data
public class UserHomeVO {

    @ApiModelProperty(value = "用户id")
    private String uid;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "学校")
    private String school;

    @ApiModelProperty(value = "个性签名")
    private String signature;

    @ApiModelProperty(value = "昵称")
    private String nickname;

    @ApiModelProperty(value = "gender")
    private String gender;

    @ApiModelProperty(value = "github地址")
    private String github;

    @ApiModelProperty(value = "博客地址")
    private String blog;

    @ApiModelProperty(value = "头像地址")
    private String avatar;

    @ApiModelProperty(value = "头衔、称号")
    private String titleName;

    @ApiModelProperty(value = "头衔、称号的颜色")
    private String titleColor;

    @ApiModelProperty(value = "总提交数")
    private Integer total;

    @ApiModelProperty(value = "cf得分")
    private Integer rating;

    @ApiModelProperty(value = "OI得分列表")
    private List<Integer> scoreList;

    @ApiModelProperty(value = "已解决题目列表")
    private List<String> solvedList;

    @ApiModelProperty(value = "难度=>[P1000,P1001]")
    private Map<Integer, List<UserHomeProblemVO>> solvedGroupByDifficulty;

    @ApiModelProperty(value = "最近上线时间")
    private Date recentLoginTime;

}