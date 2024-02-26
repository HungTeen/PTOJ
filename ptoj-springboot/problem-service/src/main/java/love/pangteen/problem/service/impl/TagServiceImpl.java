package love.pangteen.problem.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import love.pangteen.exception.StatusFailException;
import love.pangteen.api.constant.OJConstant;
import love.pangteen.problem.mapper.TagMapper;
import love.pangteen.api.pojo.entity.Tag;
import love.pangteen.problem.service.TagService;
import love.pangteen.problem.utils.ProblemUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/24 9:15
 **/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    @Override
    public Long getTagId(String name, String oj) {
        return lambdaQuery().eq(name != null, Tag::getName, name).eq(oj != null, Tag::getOj, oj).one().getId();
    }

    @Transactional
    @Override
    public void saveNewTags(List<Tag> tags) {
        if (CollUtil.isNotEmpty(tags)) {
            List<Tag> newTags = tags.stream()
                    .filter(tag -> tag.getId() == null)
                    .filter(tag -> lambdaQuery()
                            .eq(Tag::getName, tag.getName())
                            .eq(Tag::getOj, OJConstant.DEFAULT_TAG_SOURCE)
                            .oneOpt().isEmpty())
                    .collect(Collectors.toList());
            newTags.forEach(tag -> tag.setOj(OJConstant.DEFAULT_TAG_SOURCE));
            saveBatch(newTags);
        }
    }

    @Override
    public Tag addTag(Tag tag) {
        if (lambdaQuery()
                .eq(tag.getGid() != null, Tag::getGid, tag.getGid())
                .eq(Tag::getName, tag.getName())
                .eq(Tag::getOj, tag.getOj()).oneOpt().isPresent()) {
            throw new StatusFailException("该标签名称已存在！请勿重复添加！");
        }
        save(tag);
        return tag;
    }

    @Override
    public List<Tag> getAllProblemTagsList(String oj) {
        return lambdaQuery()
                .isNull(Tag::getGid)
                .eq(!ProblemUtils.forAllProblem(oj), Tag::getOj, oj.toUpperCase()).list();
    }

}
