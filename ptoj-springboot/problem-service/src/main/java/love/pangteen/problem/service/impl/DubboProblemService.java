package love.pangteen.problem.service.impl;

import cn.hutool.core.collection.CollUtil;
import love.pangteen.api.service.IDubboProblemService;
import love.pangteen.api.pojo.entity.Problem;
import love.pangteen.problem.service.ProblemService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/26 10:16
 **/
@DubboService
public class DubboProblemService implements IDubboProblemService {

    @Resource
    private ProblemService problemService;

    @Override
    public Map<Long, String> getProblemTitleMap(List<Long> pidList) {
        List<Problem> list = problemService.lambdaQuery()
                .select(Problem::getTitle, Problem::getId)
                .in(Problem::getId, pidList).list();
        return CollUtil.toMap(list, new HashMap<>(), Problem::getId, Problem::getTitle);
    }

    @Override
    public boolean canProblemShare(Long pid) {
        return problemService.lambdaQuery().select(Problem::getCodeShare).eq(Problem::getId, pid).oneOpt().map(Problem::getCodeShare).orElse(false);
    }

    @Override
    public Problem getByProblemId(String problemId) {
        return problemService.lambdaQuery().eq(Problem::getProblemId, problemId).oneOpt().orElse(null);
    }

}
