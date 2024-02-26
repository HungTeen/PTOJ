package love.pangteen.training.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseMapper;
import love.pangteen.training.pojo.entity.Training;
import love.pangteen.training.pojo.vo.TrainingVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/2/26 10:35
 **/
public interface TrainingMapper extends MPJBaseMapper<Training> {

    List<TrainingVO> getTrainingList(IPage page,
                                     @Param("categoryId") Long categoryId,
                                     @Param("auth") String auth,
                                     @Param("keyword") String keyword);
}
