package love.pangteen.problem.mapper;

import cn.hutool.core.lang.Pair;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import love.pangteen.api.pojo.entity.Judge;
import love.pangteen.api.pojo.vo.ProblemCountVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/25 16:10
 **/
public interface JudgeMapper extends BaseMapper<Judge> {

    @Select("select DISTINCT pid from judge where uid = #{uid} and status = #{status}")
    List<Long> getUserAcceptList(String uid, int status);

    @Select("select uid, count(DISTINCT pid) from judge where status = #{status} group by uid")
    List<Pair<String, Long>> getAcceptList(int status);

    List<ProblemCountVO> getProblemListCount(@Param("pidList") List<Long> pidList);


}
