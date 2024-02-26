package love.pangteen.training.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import love.pangteen.training.pojo.dto.TrainingDTO;
import love.pangteen.training.pojo.entity.Training;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/2/26 10:36
 **/
public interface TrainingService extends MPJBaseService<Training> {
    IPage<Training> getTrainingList(Integer limit, Integer currentPage, String keyword);

    TrainingDTO getTraining(Long tid);

    void deleteTraining(Long tid);

    void addTraining(TrainingDTO trainingDto);

    void updateTraining(TrainingDTO trainingDto);

    void changeTrainingStatus(Long tid, String author, Boolean status);

    void modified(Long tid);

}
