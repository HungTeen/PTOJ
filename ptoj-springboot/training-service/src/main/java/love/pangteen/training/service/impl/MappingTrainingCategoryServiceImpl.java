package love.pangteen.training.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import love.pangteen.training.mapper.MappingTrainingCategoryMapper;
import love.pangteen.training.pojo.entity.MappingTrainingCategory;
import love.pangteen.training.pojo.entity.Training;
import love.pangteen.training.pojo.entity.TrainingCategory;
import love.pangteen.training.service.MappingTrainingCategoryService;
import love.pangteen.training.service.TrainingCategoryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/2/26 11:16
 **/
@Service
public class MappingTrainingCategoryServiceImpl extends ServiceImpl<MappingTrainingCategoryMapper, MappingTrainingCategory> implements MappingTrainingCategoryService {

    @Resource
    private TrainingCategoryService categoryService;

    @Override
    public TrainingCategory getTrainingCategory(Long tid) {
        MappingTrainingCategory mappingTrainingCategory = lambdaQuery()
                .eq(MappingTrainingCategory::getTid, tid)
                .one();
        if(mappingTrainingCategory != null) {
            return categoryService.getById(mappingTrainingCategory.getCid());
        }
        return null;
    }

    @Override
    public void removeByTid(Long tid) {
        lambdaUpdate()
                .eq(MappingTrainingCategory::getTid, tid)
                .remove();
    }

    @Override
    public boolean saveTrainingCategory(Long tid, TrainingCategory category) {
        TrainingCategory trainingCategory = saveOrGet(category);
        return save(new MappingTrainingCategory().setTid(tid).setCid(trainingCategory.getId()));
    }

    @Override
    public boolean updateTrainingCategory(Training training, TrainingCategory category) {
        Long tid = training.getId();
        TrainingCategory trainingCategory = saveOrGet(category);
        MappingTrainingCategory mappingTrainingCategory = lambdaQuery()
                .eq(MappingTrainingCategory::getTid, tid)
                .one();
        if(mappingTrainingCategory == null) {
            return save(new MappingTrainingCategory().setTid(tid).setCid(trainingCategory.getId()));
        } else if(! mappingTrainingCategory.getCid().equals(trainingCategory.getId())){
            if(lambdaUpdate()
                    .eq(MappingTrainingCategory::getTid, tid)
                    .set(MappingTrainingCategory::getCid, trainingCategory.getId())
                    .update()){
                // TODO adminTrainingRecordManager.checkSyncRecord(training);
            } else {
                return false;
            }
        }
        return true;
    }

    public TrainingCategory saveOrGet(TrainingCategory category){
        // 不存在则添加。
        TrainingCategory trainingCategory = category;
        if(category.getId() == null){
            try {
                categoryService.save(category);
            } catch (Exception ignored) {
                trainingCategory = categoryService.getByName(category.getName());
            }
        }
        return trainingCategory;
    }
}
