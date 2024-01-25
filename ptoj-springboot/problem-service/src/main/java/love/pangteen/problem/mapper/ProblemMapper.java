package love.pangteen.problem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import love.pangteen.problem.pojo.entity.Problem;
import love.pangteen.problem.pojo.vo.ProblemVO;

import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/22 8:47
 **/
public interface ProblemMapper extends BaseMapper<Problem> {

    List<ProblemVO> getProblemList(Page<ProblemVO> page, Long pid, String keyword, Integer difficulty, List<Long> tagIds, Integer tagListSize, String oj);
}
