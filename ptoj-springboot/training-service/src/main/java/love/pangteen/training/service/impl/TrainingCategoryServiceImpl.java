package love.pangteen.training.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import love.pangteen.exception.StatusFailException;
import love.pangteen.training.mapper.TrainingCategoryMapper;
import love.pangteen.training.pojo.entity.TrainingCategory;
import love.pangteen.training.service.TrainingCategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/2/26 11:16
 **/
@Service
public class TrainingCategoryServiceImpl extends ServiceImpl<TrainingCategoryMapper, TrainingCategory> implements TrainingCategoryService {

    @Override
    public TrainingCategory addTrainingCategory(TrainingCategory trainingCategory) {
        if(lambdaQuery()
                .eq(trainingCategory.getGid() != null, TrainingCategory::getGid, trainingCategory.getGid())
                .eq(TrainingCategory::getName, trainingCategory.getName())
                .oneOpt().isPresent()){
            throw new StatusFailException("该分类名称已存在！请勿重复添加！");
        }
        boolean isOk = save(trainingCategory);
        if (!isOk) {
            throw new StatusFailException("添加失败");
        }
        return trainingCategory;
    }

    @Override
    public void updateTrainingCategory(TrainingCategory trainingCategory) {
        boolean isOk = updateById(trainingCategory);
        if (!isOk) {
            throw new StatusFailException("更新失败！");
        }
    }

    @Override
    public void deleteTrainingCategory(Long cid) {
        boolean isOk = removeById(cid);
        if (!isOk) {
            throw new StatusFailException("删除失败！");
        }
    }

    @Override
    public TrainingCategory getByName(String name) {
        return lambdaQuery().eq(TrainingCategory::getName, name).oneOpt().orElse(null);
    }

    @Override
    public List<TrainingCategory> getTrainingCategory() {
        return lambdaQuery().list();
    }

}
