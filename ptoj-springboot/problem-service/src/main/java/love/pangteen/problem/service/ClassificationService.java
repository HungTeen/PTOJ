package love.pangteen.problem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import love.pangteen.problem.pojo.entity.TagClassification;
import love.pangteen.problem.pojo.vo.ProblemTagVO;

import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/24 11:28
 **/
public interface ClassificationService extends IService<TagClassification> {
    List<TagClassification> getTagClassification(String oj);

    TagClassification addTagClassification(TagClassification tagClassification);

    List<ProblemTagVO> getProblemTagsAndClassification(String oj);
}
