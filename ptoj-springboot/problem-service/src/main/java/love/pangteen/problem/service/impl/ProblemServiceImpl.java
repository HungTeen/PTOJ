package love.pangteen.problem.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import love.pangteen.api.enums.JudgeMode;
import love.pangteen.api.enums.RemoteOJ;
import love.pangteen.exception.StatusFailException;
import love.pangteen.problem.mapper.ProblemMapper;
import love.pangteen.problem.pojo.dto.ProblemDTO;
import love.pangteen.problem.pojo.entity.CodeTemplate;
import love.pangteen.problem.pojo.entity.Language;
import love.pangteen.problem.pojo.entity.Problem;
import love.pangteen.problem.pojo.entity.ProblemLanguage;
import love.pangteen.problem.service.CodeTemplateService;
import love.pangteen.problem.service.ProblemCaseService;
import love.pangteen.problem.service.ProblemLanguageService;
import love.pangteen.problem.service.ProblemService;
import love.pangteen.problem.utils.ValidateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/22 8:48
 **/
@Service
public class ProblemServiceImpl extends ServiceImpl<ProblemMapper, Problem> implements ProblemService {

    @Resource
    private ValidateUtils validateUtils;

    @Resource
    private ProblemLanguageService problemLanguageService;

    @Resource
    private CodeTemplateService codeTemplateService;

    @Resource
    private ProblemCaseService problemCaseService;

    @Override
    public IPage<Problem> getProblemList(Integer limit, Integer currentPage, String keyword, Integer auth, String oj) {
        return lambdaQuery()
                .eq(Problem::getIsGroup, false)
                .and(forAllProblem(oj), wrapper -> {
                    if (!RemoteOJ.isRemoteOJ(oj)) {
                        wrapper.eq(Problem::getIsRemote, false);
                    } else {
                        wrapper.eq(Problem::getIsRemote, true).likeRight(Problem::getProblemId, oj);
                    }
                })
                .and(StrUtil.isNotEmpty(keyword), wrapper -> {
                    final String key = keyword.trim();
                    wrapper.like(Problem::getTitle, key).or()
                            .like(Problem::getAuthor, key).or()
                            .like(Problem::getProblemId, key);
                })
                .eq(auth != null && auth != 0, Problem::getAuth, auth)
                .orderByDesc(Problem::getId)
                .page(new Page<>(currentPage, limit));
    }

    @Override
    public Problem getProblem(Long pid) {
        return lambdaQuery().eq(Problem::getProblemId, pid).oneOpt().orElseThrow(() -> new StatusFailException("查询失败！"));
    }

    @Override
    public void deleteProblem(Long pid) {
        boolean success = remove(new LambdaQueryWrapper<>(Problem.class)
                .eq(Problem::getProblemId, pid));
        if(success){
            //TODO 删除对于题目的文件。
//            FileUtil.del(Constants.File.TESTCASE_BASE_FOLDER.getPath() + File.separator + "problem_" + pid);
//            AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
//            log.info("[{}],[{}],pid:[{}],operatorUid:[{}],operatorUsername:[{}]",
//                    "Admin_Problem", "Delete", pid, userRolesVo.getUid(), userRolesVo.getUsername());
        } else {
            throw new StatusFailException("删除失败！");
        }
    }

    @Transactional
    @Override
    public void addProblem(ProblemDTO problemDto) {
        Problem problem = problemDto.getProblem();
        validateUtils.validateJudgeCaseMode(problem);
        if(lambdaQuery().eq(Problem::getProblemId, problem.getProblemId().toUpperCase()).oneOpt().isPresent()){
            throw new StatusFailException("该题目的Problem ID已存在，请更换！");
        }

        // 设置Problem其他属性，然后保存。
        if (JudgeMode.DEFAULT.getMode().equals(problemDto.getJudgeMode())) {
            problem.setSpjLanguage(null).setSpjCode(null);
        }
        problem.setCaseVersion(String.valueOf(System.currentTimeMillis()));
        if (problem.getIsGroup() == null) {
            problem.setIsGroup(false);
        }
        problem.setProblemId(problem.getProblemId().toUpperCase());
        save(problem);

        // 为新的题目添加对应的language。
        List<ProblemLanguage> problemLanguageList = new ArrayList<>();
        for (Language language : problemDto.getLanguages()) {
            problemLanguageList.add(new ProblemLanguage().setPid(problem.getId()).setLid(language.getId()));
        }
        problemLanguageService.saveBatch(problemLanguageList);

        // 为新的题目添加对应的codeTemplate。
        if (CollUtil.isNotEmpty(problemDto.getCodeTemplates())) {
            for (CodeTemplate codeTemplate : problemDto.getCodeTemplates()) {
                codeTemplate.setPid(problem.getId());
            }
            codeTemplateService.saveBatch(problemDto.getCodeTemplates());
        }

        // 为新的题目添加对应的case。
//        if (problemDto.getIsUploadTestCase()) { // 如果是选择上传测试文件的，则需要遍历对应文件夹，读取数据。。
//            int sumScore = 0;
//            String testcaseDir = problemDto.getUploadTestcaseDir();
//            // 如果是io题目统计总分
//            List<ProblemCase> problemCases = problemDto.getSamples();
//            if (problemCases.isEmpty()) {
//                throw new RuntimeException("The test cases of problem must not be empty!");
//            }
//            for (ProblemCase problemCase : problemCases) {
//                if (problemCase.getScore() != null) {
//                    sumScore += problemCase.getScore();
//                }
//                if (StringUtils.isEmpty(problemCase.getOutput())) {
//                    String filePreName = problemCase.getInput().split("\\.")[0];
//                    problemCase.setOutput(filePreName + ".out");
//                }
//                problemCase.setPid(pid);
//            }
//            // 设置oi总分数，根据每个测试点的加和
//            if (ProblemType.isOIType(problem.getType())) {
//                UpdateWrapper<Problem> problemUpdateWrapper = new UpdateWrapper<>();
//                problemUpdateWrapper.eq("id", pid)
//                        .set("io_score", sumScore);
//                problemMapper.update(null, problemUpdateWrapper);
//            }
//            problemCaseService.saveBatch(problemCases);
//            // 异步上传用例。
//            problemCaseService.initUploadTestCase(
//                    problemDto.getJudgeMode(),
//                    problem.getJudgeCaseMode(),
//                    problem.getCaseVersion(),
//                    problem.getId(),
//                    testcaseDir,
//                    problemDto.getSamples()
//            );
//        } else {
//            // oi题目需要求取平均值，给每个测试点初始oi的score值，默认总分100分
//            if (problem.getType().intValue() == Constants.Contest.TYPE_OI.getCode()) {
//                for (ProblemCase problemCase : problemDto.getSamples()) {
//                    // 设置好新题目的pid和累加总分数
//                    problemCase.setPid(pid);
//                }
//                int sumScore = calProblemTotalScore(problem.getJudgeCaseMode(), problemDto.getSamples());
//                addCasesToProblemResult = problemCaseEntityService.saveOrUpdateBatch(problemDto.getSamples());
//                UpdateWrapper<Problem> problemUpdateWrapper = new UpdateWrapper<>();
//                problemUpdateWrapper.eq("id", pid)
//                        .set("io_score", sumScore);
//                problemMapper.update(null, problemUpdateWrapper);
//            } else {
//                problemDto.getSamples().forEach(problemCase -> problemCase.setPid(pid)); // 设置好新题目的pid
//                addCasesToProblemResult = problemCaseEntityService.saveOrUpdateBatch(problemDto.getSamples());
//            }
//            initHandTestCase(problemDto.getJudgeMode(),
//                    problem.getJudgeCaseMode(),
//                    problem.getCaseVersion(),
//                    pid,
//                    problemDto.getSamples());
//        }
    }

    private boolean forAllProblem(String oj){
        return oj != null && !"All".equals(oj);
    }

}
