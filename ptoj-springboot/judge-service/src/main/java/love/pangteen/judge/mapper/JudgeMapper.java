package love.pangteen.judge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import love.pangteen.api.pojo.entity.Judge;
import org.apache.ibatis.annotations.Select;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/30 15:30
 **/
public interface JudgeMapper extends BaseMapper<Judge> {

    @Select("SELECT COUNT(DISTINCT pid) FROM judge WHERE uid = #{uid} AND status = #{status}")
    int getJudgeCount(String uid, int status);

}
