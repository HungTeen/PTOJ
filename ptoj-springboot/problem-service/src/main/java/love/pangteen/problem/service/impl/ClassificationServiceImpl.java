package love.pangteen.problem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import love.pangteen.exception.StatusFailException;
import love.pangteen.problem.mapper.ClassificationMapper;
import love.pangteen.problem.pojo.entity.TagClassification;
import love.pangteen.problem.service.ClassificationService;
import love.pangteen.problem.utils.ProblemUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/24 11:28
 **/
@Service
public class ClassificationServiceImpl extends ServiceImpl<ClassificationMapper, TagClassification> implements ClassificationService {

    @Override
    public List<TagClassification> getTagClassification(String oj) {
        return lambdaQuery().eq(!ProblemUtils.forAllProblem(oj), TagClassification::getOj, oj).list();
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

}
