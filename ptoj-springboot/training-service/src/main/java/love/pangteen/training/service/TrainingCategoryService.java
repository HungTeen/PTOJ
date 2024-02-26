package love.pangteen.training.service;

import com.baomidou.mybatisplus.extension.service.IService;
import love.pangteen.training.pojo.entity.TrainingCategory;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/2/26 11:15
 **/
public interface TrainingCategoryService extends IService<TrainingCategory> {
    TrainingCategory addTrainingCategory(TrainingCategory trainingCategory);

    void updateTrainingCategory(TrainingCategory trainingCategory);

    void deleteTrainingCategory(Long cid);

    TrainingCategory getByName(String name);
}
