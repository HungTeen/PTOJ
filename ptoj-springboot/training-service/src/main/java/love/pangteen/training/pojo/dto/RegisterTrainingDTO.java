package love.pangteen.training.pojo.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/2/26 15:16
 **/
@Data
public class RegisterTrainingDTO {

    @NotNull(message = "tid不能为空")
    @NotBlank(message = "tid不能为空")
    private Long tid;

    @NotNull(message = "password")
    @NotBlank(message = "password不能为空")
    private String password;
}
