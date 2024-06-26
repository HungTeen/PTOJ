package love.pangteen.problem.pojo.vo;

import lombok.Data;
import love.pangteen.api.pojo.entity.JudgeCase;

import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/25 16:37
 **/
@Data
public class JudgeCaseVO {

    /**
     * 当judgeCaseMode为default时
     */
    private List<JudgeCase> judgeCaseList;

    /**
     * 当judgeCaseMode为subtask_lowest,subtask_average时
     */
    private List<SubTaskJudgeCaseVO> subTaskJudgeCaseVoList;

    /**
     * default,subtask_lowest,subtask_average
     */
    private String judgeCaseMode;
}
