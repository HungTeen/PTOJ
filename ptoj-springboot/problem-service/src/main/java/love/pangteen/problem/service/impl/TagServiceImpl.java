package love.pangteen.problem.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import love.pangteen.exception.StatusFailException;
import love.pangteen.problem.constants.OJConstants;
import love.pangteen.problem.mapper.TagMapper;
import love.pangteen.problem.pojo.entity.Tag;
import love.pangteen.problem.service.TagService;
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

    @Transactional
    @Override
    public void saveNewTags(List<Tag> tags) {
        if (CollUtil.isNotEmpty(tags)) {
            List<Tag> newTags = tags.stream()
                    .filter(tag -> tag.getId() == null)
                    .filter(tag -> lambdaQuery()
                            .eq(Tag::getName, tag.getName())
                            .eq(Tag::getOj, OJConstants.DEFAULT_TAG_SOURCE)
                            .oneOpt().isEmpty())
                    .collect(Collectors.toList());
            newTags.forEach(tag -> tag.setOj(OJConstants.DEFAULT_TAG_SOURCE));
            saveBatch(newTags);
        }
    }

    @Override
    public Tag addTag(Tag tag) {
        if(lambdaQuery()
                .eq(tag.getGid() != null, Tag::getGid, tag.getGid())
                .eq(Tag::getName, tag.getName())
                .eq(Tag::getOj, tag.getOj()).oneOpt().isPresent()){
            throw new StatusFailException("该标签名称已存在！请勿重复添加！");
        }
        save(tag);
        return tag;
    }

}
