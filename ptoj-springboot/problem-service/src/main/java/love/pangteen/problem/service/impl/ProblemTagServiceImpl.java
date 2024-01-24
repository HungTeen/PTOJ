package love.pangteen.problem.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import love.pangteen.problem.mapper.ProblemTagMapper;
import love.pangteen.problem.pojo.entity.ProblemTag;
import love.pangteen.problem.pojo.entity.Tag;
import love.pangteen.problem.service.ProblemTagService;
import love.pangteen.problem.service.TagService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/24 8:56
 **/
@Service
public class ProblemTagServiceImpl extends ServiceImpl<ProblemTagMapper, ProblemTag> implements ProblemTagService {

    @Resource
    private TagService tagService;

    @Transactional
    @Override
    public void saveOrUpdateProblemTags(Long pid, List<Tag> tags) {
        if (CollUtil.isNotEmpty(tags)) {
            tagService.saveNewTags(tags);
            List<ProblemTag> problemTagList = tags.stream().map(tag -> {
                return new ProblemTag().setTid(tag.getId()).setPid(pid);
            }).collect(Collectors.toList());

            saveOrUpdateBatch(problemTagList);
        }
    }

    @Transactional
    @Override
    public void updateProblemTags(Long pid, List<Tag> tags) {
        Set<Long> oldIds = lambdaQuery().eq(ProblemTag::getPid, pid).list().stream().map(ProblemTag::getTid).collect(Collectors.toSet());
        Set<Long> newIds = tags.stream().map(Tag::getId).collect(Collectors.toSet());
        List<Long> invalidIds = oldIds.stream().filter(id -> !newIds.contains(id)).collect(Collectors.toList());
        removeByIds(invalidIds);
        saveOrUpdateProblemTags(pid, tags);
    }

    @Override
    public void deleteProblemTags(Long pid) {
        remove(new LambdaQueryWrapper<>(ProblemTag.class).eq(ProblemTag::getPid, pid));
    }

}
