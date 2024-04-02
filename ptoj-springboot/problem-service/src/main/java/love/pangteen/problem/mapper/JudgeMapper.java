package love.pangteen.problem.mapper;

import cn.hutool.core.lang.Pair;
import com.github.yulichang.base.MPJBaseMapper;
import love.pangteen.api.pojo.entity.Judge;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/25 16:10
 **/
public interface JudgeMapper extends MPJBaseMapper<Judge> {

    @Select("select DISTINCT pid from judge where uid = #{uid} and status = #{status}")
    List<Long> getUserAcceptList(String uid, int status);

    @Select("select uid, count(DISTINCT pid) from judge where status = #{status} group by uid")
    List<Pair<String, Long>> getAcceptList(int status);

}
