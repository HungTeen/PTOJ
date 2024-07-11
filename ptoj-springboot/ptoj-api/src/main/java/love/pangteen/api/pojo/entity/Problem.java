package love.pangteen.api.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import love.pangteen.api.interfaces.ValidateGroups;
import love.pangteen.api.constant.OJConstant;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.groups.Default;
import java.io.Serializable;
import java.util.Date;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/21 22:51
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "题目", description = "")
public class Problem implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    @NotNull(message = "题目的id不能为空！", groups = {ValidateGroups.Update.class})
    private Long id;

    @ApiModelProperty(value = "题目的自定义ID 例如（HOJ-1000）")
    @NotBlank(message = "题目的展示ID不能为空！", groups = {Default.class, ValidateGroups.Group.class})
    @Length(max = 50, message = "题目的展示ID的内容长度超过限制，请重新编辑！", groups = {ValidateGroups.Group.class})
    private String problemId;

    @ApiModelProperty(value = "题目")
    @NotNull(message = "题目的标题不能为空！", groups = {ValidateGroups.Group.class})
    @Length(max = 255, message = "题目的标题的内容长度超过限制，请重新编辑！", groups = {ValidateGroups.Group.class})
    private String title;

    @ApiModelProperty(value = "作者")
    private String author;

    @ApiModelProperty(value = "0为ACM,1为OI")
    @NotNull
    @Range(min = 0, max = 1, message = "题目的类型必须为ACM(0), OI(1)！")
    private Integer type;

    @ApiModelProperty(value = "default,spj,interactive")
    @Pattern(regexp = "^(default|spj|interactive)$", message = "题目的判题模式必须为普通判题(default), 特殊判题(spj), 交互判题(interactive)！")
    private String judgeMode;

    @ApiModelProperty(value = "default,subtask_lowest,subtask_average")
    @Pattern(regexp = "^(default|subtask_lowest|subtask_average)$", message = "题目的用例模式不正确！")
    private String judgeCaseMode;

    @ApiModelProperty(value = "单位ms")
    @NotNull(message = "题目的时间限制不能为空！", groups = {ValidateGroups.Group.class})
    @Range(min = 1, max = OJConstant.MAX_TIME_LIMIT, message = "题目的时间限制范围请合理填写！(1~30000ms)")
    private Integer timeLimit;

    @ApiModelProperty(value = "单位mb")
    @NotNull(message = "题目的内存限制不能为空！", groups = {ValidateGroups.Group.class})
    @Range(min = 1, max = OJConstant.MAX_MEMORY_LIMIT, message = "题目的内存限制范围请合理填写！(1~1024mb)")
    private Integer memoryLimit;

    @ApiModelProperty(value = "单位mb")
    @NotNull(message = "题目的栈限制不能为空！", groups = {ValidateGroups.Group.class})
    @Range(min = 1, max = OJConstant.MAX_STACK_LIMIT, message = "题目的栈限制范围请合理填写！(1~1024mb)")
    private Integer stackLimit;

    @ApiModelProperty(value = "描述")
    @Length(max = 65535, message = "题目的描述的内容长度超过限制，请重新编辑！", groups = {ValidateGroups.Group.class})
    private String description;

    @ApiModelProperty(value = "输入描述")
    @Length(max = 65535, message = "题目的输入描述的内容长度超过限制，请重新编辑！", groups = {ValidateGroups.Group.class})
    private String input;

    @ApiModelProperty(value = "输出描述")
    @Length(max = 65535, message = "题目的输出描述的内容长度超过限制，请重新编辑！", groups = {ValidateGroups.Group.class})
    private String output;

    @ApiModelProperty(value = "题面样例")
    private String examples;

    @ApiModelProperty(value = "是否为vj判题")
    private Boolean isRemote;

    @ApiModelProperty(value = "题目来源（vj判题时例如HDU-1000的链接）")
    private String source;

    @ApiModelProperty(value = "题目难度")
    private Integer difficulty;

    @ApiModelProperty(value = "备注,提醒")
    @Length(max = 255, message = "题目的备注的内容长度超过限制，请重新编辑！", groups = {ValidateGroups.Group.class})
    private String hint;

    @ApiModelProperty(value = "默认为1公开，2为私有，3为比赛中")
    @NotNull
    @Range(min = 1, max = 3, message = "题目的权限必须为公开题目(1), 隐藏题目(2), 比赛题目(3)！")
    private Integer auth;

    @ApiModelProperty(value = "当该题目为oi题目时的分数")
    private Integer ioScore;

    @ApiModelProperty(value = "该题目对应的相关提交代码，用户是否可用分享")
    private Boolean codeShare;

    @ApiModelProperty(value = "特判程序或交互程序的代码")
    @TableField(value = "spj_code", updateStrategy = FieldStrategy.IGNORED)
    private String spjCode;

    @ApiModelProperty(value = "特判程序或交互程序的语言")
    @TableField(value = "spj_language", updateStrategy = FieldStrategy.IGNORED)
    private String spjLanguage;

    @ApiModelProperty(value = "特判程序或交互程序的额外文件 json key:name value:content")
    @TableField(value = "user_extra_file", updateStrategy = FieldStrategy.IGNORED)
    private String userExtraFile;

    @ApiModelProperty(value = "特判程序或交互程序的额外文件 json key:name value:content")
    @TableField(value = "judge_extra_file", updateStrategy = FieldStrategy.IGNORED)
    private String judgeExtraFile;

    @ApiModelProperty(value = "是否默认去除用户代码的每行末尾空白符")
    private Boolean isRemoveEndBlank;

    @ApiModelProperty(value = "是否默认开启该题目的测试样例结果查看")
    private Boolean openCaseResult;

    @ApiModelProperty(value = "题目测试数据是否是上传的")
    private Boolean isUploadCase;

    @ApiModelProperty(value = "题目测试数据的版本号")
    private String caseVersion;

    @ApiModelProperty(value = "修改题目的管理员用户名")
    private String modifiedUser;

    @ApiModelProperty(value = "是否为团队内的题目")
    private Boolean isGroup;

    @ApiModelProperty(value = "团队ID")
    private Long gid;

    @ApiModelProperty(value = "申请公开的进度：null为未申请，1为申请中，2为申请通过，3为申请拒绝")
    private Integer applyPublicProgress;

    @ApiModelProperty(value = "是否是file io自定义输入输出文件模式")
    @TableField(value = "is_file_io")
    private Boolean isFileIO;

    @ApiModelProperty(value = "题目指定的file io输入文件的名称")
    @TableField(value = "io_read_file_name")
    private String ioReadFileName;

    @ApiModelProperty(value = "题目指定的file io输出文件的名称")
    @TableField(value = "io_write_file_name")
    private String ioWriteFileName;

    @TableField(fill = FieldFill.INSERT)
    private Date gmtCreate;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;
}
