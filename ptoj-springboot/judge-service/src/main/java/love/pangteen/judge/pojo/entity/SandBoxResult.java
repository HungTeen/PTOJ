package love.pangteen.judge.pojo.entity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/2/1 10:55
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Builder
public class SandBoxResult {
    /**
     * 单个程序的状态码
     */
    private Integer status;

    /**
     * 原沙盒输出的状态字符
     */
    private String originalStatus;

    /**
     * 单个程序的退出码
     */
    private Long exitCode;

    /**
     * 单个程序的运行所耗空间 kb
     */
    private Long memory;

    /**
     * 单个程序的运行所耗时间 ms
     */
    private Long time;

    /**
     * 单个程序的标准输出
     */
    private String stdout;

    /**
     * 单个程序的错误信息
     */
    private String stderr;
}
