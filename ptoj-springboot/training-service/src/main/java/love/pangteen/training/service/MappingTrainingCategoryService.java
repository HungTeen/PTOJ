package love.pangteen.training.service;

import com.baomidou.mybatisplus.extension.service.IService;
import love.pangteen.training.pojo.entity.MappingTrainingCategory;
import love.pangteen.training.pojo.entity.Training;
import love.pangteen.training.pojo.entity.TrainingCategory;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/2/26 11:15
 **/
public interface MappingTrainingCategoryService extends IService<MappingTrainingCategory> {

    TrainingCategory getTrainingCategory(Long tid);

    void removeByTid(Long tid);

    boolean saveTrainingCategory(Long tid, TrainingCategory category);

    boolean updateTrainingCategory(Training training, TrainingCategory trainingCategory);
}
