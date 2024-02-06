package love.pangteen.problem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import love.pangteen.problem.pojo.entity.ProblemTag;
import love.pangteen.problem.pojo.entity.Tag;

import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/24 8:55
 **/
public interface ProblemTagService extends IService<ProblemTag> {
    void saveOrUpdateProblemTags(Long id, List<Tag> tags);

    void updateProblemTags(Long id, List<Tag> tags);

    void deleteProblemTags(Long pid);

    List<Tag> getProblemTags(Long pid);
}
