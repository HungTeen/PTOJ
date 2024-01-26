package love.pangteen.api.service;

import love.pangteen.api.pojo.entity.Problem;

import java.util.List;
import java.util.Map;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/26 10:15
 **/
public interface IDubboProblemService {

    Map<Long, String> getProblemTitleMap(List<Long> pidList);

    boolean canProblemShare(Long pid);

    Problem getByProblemId(String problemId);

}