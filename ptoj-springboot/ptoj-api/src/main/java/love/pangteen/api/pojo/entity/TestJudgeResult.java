package love.pangteen.api.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/2/23 16:59
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TestJudgeResult implements Serializable {

    /**
     *  评测结果状态码
     */
    private Integer status;

    /**
     *  评测运行时间消耗 ms
     */
    private Long time;

    /**
     *  评测运行空间消耗 kb
     */
    private Long memory;

    /**
     * 输入
     */
    private String Input;

    /**
     * 期望输出
     */
    private String expectedOutput;

    /**
     * 运行标准输出
     */
    private String stdout;

    /**
     * 运行错误输出
     */
    private String stderr;

    /**
     * 原题的评测模式：default、spj、interactive
     */
    private String problemJudgeMode;
}
