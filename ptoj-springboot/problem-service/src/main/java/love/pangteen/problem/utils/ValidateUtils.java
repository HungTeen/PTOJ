package love.pangteen.problem.utils;

import love.pangteen.api.enums.JudgeCaseMode;
import love.pangteen.api.enums.ProblemType;
import love.pangteen.exception.StatusFailException;
import love.pangteen.problem.pojo.entity.Problem;
import org.springframework.stereotype.Component;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/22 10:21
 **/
@Component
public class ValidateUtils {

    public void validateJudgeCaseMode(Problem problem){
        ProblemType type = ProblemType.getProblemType(problem.getType());
        JudgeCaseMode mode = JudgeCaseMode.getJudgeCaseMode(problem.getJudgeCaseMode());
        if (type == ProblemType.ACM) {
            if (mode != JudgeCaseMode.DEFAULT && mode != JudgeCaseMode.ERGODIC_WITHOUT_ERROR) {
                throw new StatusFailException("题目的用例模式错误，ACM类型的题目只能为默认模式(default)、遇错止评(ergodic_without_error)！");
            }
        } else {
            if (mode != JudgeCaseMode.DEFAULT && mode != JudgeCaseMode.SUBTASK_AVERAGE && mode != JudgeCaseMode.SUBTASK_LOWEST) {
                throw new StatusFailException("题目的用例模式错误，OI类型的题目只能为默认模式(default)、子任务（最低得分）(subtask_lowest)、 子任务（平均得分）(subtask_average)！");
            }
        }
    }

    public interface Group {

    }

    public interface Update {

    }
}
