package love.pangteen.training.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import love.pangteen.training.pojo.entity.Training;
import love.pangteen.training.pojo.vo.TrainingVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/2/26 10:35
 **/
public interface TrainingMapper extends BaseMapper<Training> {

    List<TrainingVO> getTrainingList(IPage page,
                                     @Param("categoryId") Long categoryId,
                                     @Param("auth") String auth,
                                     @Param("keyword") String keyword);
}
