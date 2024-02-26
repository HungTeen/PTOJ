package love.pangteen.problem.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import love.pangteen.exception.StatusFailException;
import love.pangteen.problem.mapper.ClassificationMapper;
import love.pangteen.api.pojo.entity.Tag;
import love.pangteen.problem.pojo.entity.TagClassification;
import love.pangteen.problem.pojo.vo.ProblemTagVO;
import love.pangteen.problem.service.ClassificationService;
import love.pangteen.problem.service.TagService;
import love.pangteen.problem.utils.ProblemUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/24 11:28
 **/
@Service
public class ClassificationServiceImpl extends ServiceImpl<ClassificationMapper, TagClassification> implements ClassificationService {

    @Resource
    private TagService tagService;

    @Override
    public List<TagClassification> getTagClassification(String oj) {
        return lambdaQuery().eq(!ProblemUtils.forAllProblem(oj), TagClassification::getOj, oj).orderByAsc(TagClassification::getRank).list();
    }

    @Override
    public TagClassification addTagClassification(TagClassification tagClassification) {
        if(lambdaQuery()
                .eq(TagClassification::getName, tagClassification.getName())
                .eq(TagClassification::getOj, tagClassification.getOj()).oneOpt().isPresent()){
            throw new StatusFailException("该标签分类名称已存在！请勿重复！");
        }
        save(tagClassification);
        return tagClassification;
    }

    @Transactional
    @Override
    public List<ProblemTagVO> getProblemTagsAndClassification(String oj) {
        List<ProblemTagVO> problemTagVOList = new ArrayList<>();
        List<Tag> tagList = tagService.getAllProblemTagsList(oj.toUpperCase());
        List<TagClassification> classifications = getTagClassification(oj.toUpperCase());
        if (CollUtil.isEmpty(classifications)) {
            ProblemTagVO problemTagVo = new ProblemTagVO();
            problemTagVo.setTagList(tagList);
            problemTagVOList.add(problemTagVo);
        } else {
            for (TagClassification classification : classifications) {
                ProblemTagVO problemTagVo = new ProblemTagVO();
                problemTagVo.setClassification(classification);
                List<Tag> tags = new ArrayList<>();
                if (CollUtil.isNotEmpty(tagList)) {
                    Iterator<Tag> it = tagList.iterator();
                    while (it.hasNext()) {
                        Tag tag = it.next();
                        if (classification.getId().equals(tag.getTcid())) {
                            tags.add(tag);
                            it.remove();
                        }
                    }
                }
                problemTagVo.setTagList(tags);
                problemTagVOList.add(problemTagVo);
            }
            if (!tagList.isEmpty()) {
                ProblemTagVO problemTagVo = new ProblemTagVO();
                problemTagVo.setTagList(tagList);
                problemTagVOList.add(problemTagVo);
            }
        }

        if (ProblemUtils.forAllProblem(oj)) {
            problemTagVOList.sort(ProblemUtils.PROBLEM_TAG_VO_COMPARATOR);
        }
        return problemTagVOList;
    }

}
