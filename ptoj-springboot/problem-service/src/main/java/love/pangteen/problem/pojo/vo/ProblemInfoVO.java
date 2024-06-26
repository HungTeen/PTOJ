package love.pangteen.problem.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import love.pangteen.api.pojo.entity.Problem;
import love.pangteen.api.pojo.entity.Tag;
import love.pangteen.api.pojo.vo.ProblemCountVO;

import java.util.HashMap;
import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/24 22:42
 **/
@Data
@Builder
@AllArgsConstructor
public class ProblemInfoVO {
    /**
     * 题目内容
     */
    private Problem problem;
    /**
     * 题目标签
     */
    private List<Tag> tags;
    /**
     * 题目可用编程语言
     */
    private List<String> languages;
    /**
     * 题目提交统计情况
     */
    private ProblemCountVO problemCount;
    /**
     * 题目默认模板
     */
    private HashMap<String, String> codeTemplate;
}
