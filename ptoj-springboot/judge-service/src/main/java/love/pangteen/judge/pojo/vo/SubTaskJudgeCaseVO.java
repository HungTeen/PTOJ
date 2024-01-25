package love.pangteen.judge.pojo.vo;

import lombok.Data;
import love.pangteen.judge.pojo.entity.JudgeCase;

import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/25 16:38
 **/
@Data
public class SubTaskJudgeCaseVO {


    /**
     * 该subtask的分组编号
     */
    private Integer groupNum;

    /**
     * 该subtask最终结果所用时间ms
     */
    private Integer time;

    /**
     * 该subtask最终结果所用空间KB
     */
    private Integer memory;

    /**
     * 该subtask最终结果所得分数
     */
    private Integer score;

    /**
     * 该subtask最终结果状态码
     */
    private Integer status;

    /**
     * 该subtask最终AC测试点的个数
     */
    private Integer ac;

    /**
     * 该subtask的测试点总数
     */
    private Integer total;

    /**
     * 该subtask下各个测试点的具体结果
     */
    List<JudgeCase> subtaskDetailList;
}
