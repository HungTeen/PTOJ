package love.pangteen.problem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import love.pangteen.problem.pojo.entity.Tag;

import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/24 9:15
 **/
public interface TagService extends IService<Tag> {
    void saveNewTags(List<Tag> tags);

    Tag addTag(Tag tag);

    List<Tag> getAllProblemTagsList(String oj);
}
