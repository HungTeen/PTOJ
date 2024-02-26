package love.pangteen.training.pojo.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/2/26 16:00
 **/
@Data
public class TrainingProblemDTO {

    @NotBlank(message = "题目id不能为空")
    private Long pid;

    @NotBlank(message = "训练id不能为空")
    private Long tid;

    @NotBlank(message = "题目在训练中的展示id不能为空")
    private String displayId;
}
