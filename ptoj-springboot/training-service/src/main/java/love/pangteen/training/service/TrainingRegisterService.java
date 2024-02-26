package love.pangteen.training.service;

import com.github.yulichang.base.MPJBaseService;
import love.pangteen.api.pojo.vo.AccessVO;
import love.pangteen.training.pojo.dto.RegisterTrainingDTO;
import love.pangteen.training.pojo.entity.TrainingRegister;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/2/26 10:36
 **/
public interface TrainingRegisterService extends MPJBaseService<TrainingRegister> {

    void registerTraining(RegisterTrainingDTO registerTrainingDto);

    void removeByTid(Long id);

    AccessVO getTrainingAccess(Long tid);
}
