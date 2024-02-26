package love.pangteen.problem.pojo.vo;

import lombok.Data;
import love.pangteen.api.pojo.entity.Tag;
import love.pangteen.problem.pojo.entity.TagClassification;

import java.io.Serializable;
import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/25 10:16
 **/
@Data
public class ProblemTagVO implements Serializable {

    /**
     * 标签分类
     */
    private TagClassification classification;

    /**
     * 标签列表
     */
    private List<Tag> tagList;
}
