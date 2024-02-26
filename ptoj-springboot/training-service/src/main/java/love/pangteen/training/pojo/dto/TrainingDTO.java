package love.pangteen.training.pojo.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import love.pangteen.training.pojo.entity.Training;
import love.pangteen.training.pojo.entity.TrainingCategory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/2/26 10:54
 **/
@Data
@Accessors(chain = true)
public class TrainingDTO {

    @NotNull(message = "训练不能为空！")
    @Valid
    private Training training;

    private TrainingCategory trainingCategory;
}