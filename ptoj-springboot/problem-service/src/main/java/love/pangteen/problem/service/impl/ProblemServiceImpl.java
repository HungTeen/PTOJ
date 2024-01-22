package love.pangteen.problem.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import love.pangteen.api.enums.JudgeCaseMode;
import love.pangteen.api.enums.JudgeMode;
import love.pangteen.api.enums.ProblemType;
import love.pangteen.api.enums.RemoteOJ;
import love.pangteen.exception.StatusFailException;
import love.pangteen.problem.mapper.ProblemMapper;
import love.pangteen.problem.pojo.dto.ProblemDTO;
import love.pangteen.problem.pojo.entity.*;
import love.pangteen.problem.service.CodeTemplateService;
import love.pangteen.problem.service.ProblemCaseService;
import love.pangteen.problem.service.ProblemLanguageService;
import love.pangteen.problem.service.ProblemService;
import love.pangteen.problem.utils.FileUtils;
import love.pangteen.problem.utils.ValidateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
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
    private FileUtils fileUtils;

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
        if (problem.getIsGroup() == null) {
            problem.setIsGroup(false);
        }
        if (ProblemType.isOIType(problem.getType())) {
            int sumScore = calProblemTotalScore(problem.getJudgeCaseMode(), problemDto.getSamples());
            problem.setIoScore(sumScore);
        }
        problem.setCaseVersion(String.valueOf(System.currentTimeMillis()));
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
        List<ProblemCase> problemCases = problemDto.getSamples();
        problemCases.forEach(problemCase -> problemCase.setPid(problem.getId()));
        if (problemDto.getIsUploadTestCase()) { // 如果是选择上传测试文件的，则需要遍历对应文件夹，读取数据。。
            String testcaseDir = problemDto.getUploadTestcaseDir();
            for (ProblemCase problemCase : problemCases) {
                if (StrUtil.isEmpty(problemCase.getOutput())) {
                    String filePreName = problemCase.getInput().split("\\.")[0];
                    problemCase.setOutput(filePreName + ".out");
                }
            }
            // 异步上传用例。
            fileUtils.initUploadTestCase(
                    problemDto.getJudgeMode(),
                    problem.getJudgeCaseMode(),
                    problem.getCaseVersion(),
                    problem.getId(),
                    testcaseDir,
                    problemDto.getSamples()
            );
        } else {
            fileUtils.initHandTestCase(problemDto.getJudgeMode(),
                    problem.getJudgeCaseMode(),
                    problem.getCaseVersion(),
                    problem.getId(),
                    problemDto.getSamples());
        }
        problemCaseService.saveBatch(problemCases);
    }

    @Override
    public void updateProblem(ProblemDTO problemDto) {

    }

    private int calProblemTotalScore(String judgeCaseMode, List<ProblemCase> problemCaseList) {
        int sumScore = 0;
        if (JudgeCaseMode.SUBTASK_LOWEST.getMode().equals(judgeCaseMode)) {
            HashMap<Integer, Integer> groupNumMapScore = new HashMap<>();
            for (ProblemCase problemCase : problemCaseList) {
                groupNumMapScore.merge(problemCase.getGroupNum(), problemCase.getScore(), Math::min);
            }
            for (Integer minScore : groupNumMapScore.values()) {
                sumScore += minScore;
            }
        } else if (JudgeCaseMode.SUBTASK_AVERAGE.getMode().equals(judgeCaseMode)) {
            // 预处理 切换成Map Key: groupNum Value: <count,sum_score>
            HashMap<Integer, Pair<Integer, Integer>> groupNumMapScore = new HashMap<>();
            for (ProblemCase problemCase : problemCaseList) {
                Pair<Integer, Integer> pair = groupNumMapScore.get(problemCase.getGroupNum());
                if (pair == null) {
                    groupNumMapScore.put(problemCase.getGroupNum(), new Pair<>(1, problemCase.getScore()));
                } else {
                    int count = pair.getKey() + 1;
                    int score = pair.getValue() + problemCase.getScore();
                    groupNumMapScore.put(problemCase.getGroupNum(), new Pair<>(count, score));
                }
            }
            for (Pair<Integer, Integer> pair : groupNumMapScore.values()) {
                sumScore += (int) Math.round(pair.getValue() * 1.0 / pair.getKey());
            }
        } else {
            for (ProblemCase problemCase : problemCaseList) {
                if (problemCase.getScore() != null) {
                    sumScore += problemCase.getScore();
                }
            }
        }
        return sumScore;
    }

    private boolean forAllProblem(String oj){
        return oj != null && !"All".equals(oj);
    }

}
