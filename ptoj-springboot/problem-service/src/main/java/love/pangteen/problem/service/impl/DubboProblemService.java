package love.pangteen.problem.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import love.pangteen.api.pojo.entity.Problem;
import love.pangteen.api.pojo.entity.ProblemCase;
import love.pangteen.api.service.IDubboProblemService;
import love.pangteen.problem.service.ProblemCaseService;
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

    @Resource
    private ProblemCaseService problemCaseService;

    @Override
    public Map<Long, String> getProblemTitleMap(List<Long> pidList) {
        List<Problem> list = problemService.lambdaQuery()
                .select(Problem::getTitle, Problem::getId)
                .in(Problem::getId, pidList).list();
        return CollUtil.toMap(list, new HashMap<>(), Problem::getId, Problem::getTitle);
    }

    @Override
    public Problem getById(Long pid) {
        return problemService.lambdaQuery().eq(Problem::getId, pid).oneOpt().orElse(null);
    }

    @Override
    public Problem getByProblemId(String problemId) {
        return problemService.lambdaQuery().eq(Problem::getProblemId, problemId).oneOpt().orElse(null);
    }

    @Override
    public List<ProblemCase> getProblemCases(Long pid) {
        return problemCaseService.getProblemCases(pid, false);
    }

    @Override
    public boolean removeById(Long pid) {
        problemService.deleteProblem(pid);
        return true;
    }

    @Override
    public IPage<Problem> getTrainingProblemPage(Integer limit, Integer currentPage, String keyword, Boolean queryExisted, List<Long> pidList) {
        return problemService.lambdaQuery()
                .and(wrapper -> {
                    // 逻辑判断，如果是查询已有的就应该是in，如果是查询不要重复的，使用not in。
                    if (queryExisted) {
                        wrapper.in(!pidList.isEmpty(), Problem::getId, pidList)
                                .isNull(pidList.isEmpty(), Problem::getId);
                    } else {
                        // 权限需要是公开的（隐藏的，比赛中不可加入！）
                        wrapper.notIn(!pidList.isEmpty(), Problem::getId, pidList)
                                .eq(Problem::getAuth, 1).eq(Problem::getIsGroup, false);
                    }
                }).and(StrUtil.isNotEmpty(keyword), wrapper -> {
                    wrapper.like(Problem::getTitle, keyword).or().like(Problem::getProblemId, keyword).or().like(Problem::getAuthor, keyword);
                }).page(new Page<>(currentPage, limit));
    }

}
