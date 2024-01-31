package love.pangteen.problem.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import love.pangteen.problem.mapper.ProblemCaseMapper;
import love.pangteen.api.pojo.entity.Problem;
import love.pangteen.api.pojo.entity.ProblemCase;
import love.pangteen.problem.service.ProblemCaseService;
import love.pangteen.problem.utils.ProblemCaseUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/22 17:02
 **/
@Service
public class ProblemCaseServiceImpl extends ServiceImpl<ProblemCaseMapper, ProblemCase> implements ProblemCaseService {

    @Resource
    private ProblemCaseUtils problemCaseUtils;

    @Override
    public List<ProblemCase> getProblemCases(Long pid, Boolean isUpload) {
        return lambdaQuery()
                .eq(ProblemCase::getPid, pid)
                .eq(ProblemCase::getStatus, 0)
                .last(isUpload, "order by length(input) asc,input asc")
                .list();
    }

    @Transactional
    @Override
    public void saveProblemCases(Problem problem, List<ProblemCase> problemCases, boolean isUpload, String uploadTestcaseDir) {
        problemCases.forEach(problemCase -> problemCase.setPid(problem.getId()));
        if (isUpload) { // 如果是选择上传测试文件的，则需要遍历对应文件夹，读取数据。。
            for (ProblemCase problemCase : problemCases) {
                if (StrUtil.isEmpty(problemCase.getOutput())) {
                    String filePreName = problemCase.getInput().split("\\.")[0];
                    problemCase.setOutput(filePreName + ".out");
                }
            }
            problemCaseUtils.initUploadTestCase(problem.getJudgeMode(), problem.getJudgeCaseMode(), problem.getCaseVersion(), problem.getId(), uploadTestcaseDir, problemCases);
        } else {
            problemCaseUtils.initHandTestCase(problem.getJudgeMode(), problem.getJudgeCaseMode(), problem.getCaseVersion(), problem.getId(), problemCases);
        }
        saveBatch(problemCases);
    }

    @Transactional
    @Override
    public void updateProblemCases(Problem problem, List<ProblemCase> problemCases, Boolean isUploadTestCase, String uploadTestcaseDir) {
        // 如果是自家的题目才有测试数据。
        if (!problem.getIsRemote() && !problemCases.isEmpty()) {
            boolean changed = false;
            List<ProblemCase> oldProblemCases = lambdaQuery().eq(ProblemCase::getPid, problem.getId()).list();
            Set<Long> oldIds = oldProblemCases.stream().map(ProblemCase::getId).collect(Collectors.toSet());
            Set<Long> newIds = problemCases.stream().map(ProblemCase::getId).filter(Objects::nonNull).collect(Collectors.toSet());
            List<Long> invalidIds = oldIds.stream().filter(id -> {
                return !newIds.contains(id);
            }).collect(Collectors.toList());
            changed |= removeByIds(invalidIds);
            problemCases.stream().filter(problemCase -> problemCase.getId() == null).forEach(problemCase -> problemCase.setPid(problem.getId()));
            changed |= saveOrUpdateBatch(problemCases);

            if(changed){
                problem.setCaseVersion(String.valueOf(System.currentTimeMillis()));
            }

            //TODO 有问题？？？
            if (isUploadTestCase) { // 如果是选择上传测试文件的，则需要遍历对应文件夹，读取数据。。
                problemCaseUtils.initUploadTestCase(problem.getJudgeMode(), problem.getJudgeCaseMode(), problem.getCaseVersion(), problem.getId(), uploadTestcaseDir, problemCases);
            } else {
                problemCaseUtils.initHandTestCase(problem.getJudgeMode(), problem.getJudgeCaseMode(), problem.getCaseVersion(), problem.getId(), problemCases);
            }
        }
    }

    @Override
    public void deleteProblemCases(Long pid) {
        remove(new LambdaQueryWrapper<>(ProblemCase.class).eq(ProblemCase::getPid, pid));
    }
}
