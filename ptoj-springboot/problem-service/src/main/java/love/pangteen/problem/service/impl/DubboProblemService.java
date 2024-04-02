package love.pangteen.problem.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import love.pangteen.api.pojo.entity.Problem;
import love.pangteen.api.pojo.entity.ProblemCase;
import love.pangteen.api.pojo.vo.ProblemVO;
import love.pangteen.api.service.IDubboProblemService;
import love.pangteen.problem.service.ProblemCaseService;
import love.pangteen.problem.service.ProblemService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

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
    public Problem getById(Long pid) {
        return problemService.lambdaQuery().eq(Problem::getId, pid).oneOpt().orElse(null);
    }

    @Override
    public Problem getByProblemId(String problemId) {
        return problemService.getByProblemId(problemId);
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
    public PageDTO<Problem> getTrainingProblemPage(Integer limit, Integer currentPage, String keyword, Boolean queryExisted, List<Long> pidList) {
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
                }).page(new PageDTO<>(currentPage, limit));
    }

    @Override
    public List<Long> getValidPidList(List<Long> pidList) {
        return problemService.lambdaQuery()
                .eq(Problem::getAuth, 1)
                .in(Problem::getId, pidList).list()
                .stream().map(Problem::getId).collect(Collectors.toList());
    }

    @Override
    public List<ProblemVO> getTrainingProblemList(List<Long> pidList) {
        return pidList.stream().map(pid -> {
            Problem problem = problemService.getById(pid);
            ProblemVO problemVO = new ProblemVO();
            BeanUtils.copyProperties(problem, problemVO);
            return problemVO;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Problem> getProblems(List<Long> acPidList) {
        if(acPidList.isEmpty()) return List.of();
        return problemService.lambdaQuery().in(Problem::getId, acPidList).list();
    }

}
