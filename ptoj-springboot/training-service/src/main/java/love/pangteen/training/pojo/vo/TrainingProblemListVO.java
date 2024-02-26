package love.pangteen.training.pojo.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import love.pangteen.api.pojo.entity.Problem;
import love.pangteen.training.pojo.entity.TrainingProblem;

import java.util.HashMap;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/2/26 16:33
 **/
@Data
@Accessors(chain = true)
public class TrainingProblemListVO {

    @ApiModelProperty(value = "题目")
    IPage<Problem> problemList;

    @ApiModelProperty(value = "用户id")
    HashMap<Long, TrainingProblem> trainingProblemMap;

}
