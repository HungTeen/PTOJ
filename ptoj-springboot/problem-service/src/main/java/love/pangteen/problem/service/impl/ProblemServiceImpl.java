package love.pangteen.problem.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import love.pangteen.api.enums.JudgeMode;
import love.pangteen.api.enums.ProblemType;
import love.pangteen.api.enums.RemoteOJ;
import love.pangteen.exception.StatusFailException;
import love.pangteen.pojo.AccountProfile;
import love.pangteen.problem.mapper.ProblemMapper;
import love.pangteen.problem.pojo.dto.ProblemDTO;
import love.pangteen.problem.pojo.entity.Problem;
import love.pangteen.problem.pojo.entity.ProblemCase;
import love.pangteen.problem.service.*;
import love.pangteen.problem.utils.ProblemUtils;
import love.pangteen.problem.utils.ValidateUtils;
import love.pangteen.utils.AccountUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/22 8:48
 **/
@Service
public class ProblemServiceImpl extends ServiceImpl<ProblemMapper, Problem> implements ProblemService {

    @Resource
    private ProblemLanguageService problemLanguageService;

    @Resource
    private CodeTemplateService codeTemplateService;

    @Resource
    private ProblemCaseService problemCaseService;

    @Resource
    private ProblemTagService problemTagService;

    @Override
    public IPage<Problem> getProblemList(Integer limit, Integer currentPage, String keyword, Integer auth, String oj) {
        return lambdaQuery()
                .eq(Problem::getIsGroup, false)
                .and(ProblemUtils.forAllProblem(oj), wrapper -> {
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

    @Transactional
    @Override
    public void deleteProblem(Long pid) {
        removeById(pid);
        problemLanguageService.deleteProblemLanguages(pid);
        codeTemplateService.deleteCodeTemplates(pid);
        problemCaseService.deleteProblemCases(pid);
        problemTagService.deleteProblemTags(pid);
    }

    @Transactional
    @Override
    public void addProblem(ProblemDTO problemDto) {
        Problem problem = problemDto.getProblem();

        ValidateUtils.validateProblem(problem);
        if(lambdaQuery().eq(Problem::getProblemId, problem.getProblemId().toUpperCase()).oneOpt().isPresent()){
            throw new StatusFailException("该题目的Problem ID已存在，请更换！");
        }
        problem.setCaseVersion(String.valueOf(System.currentTimeMillis()));

        // 设置Problem其他属性，然后保存。
        saveOrUpdateProblem(problem, problemDto.getSamples());

        // 为新的题目添加对应的language。
        problemLanguageService.saveProblemLanguages(problem.getId(), problemDto.getLanguages());

        // 为新的题目添加对应的codeTemplate。
        codeTemplateService.saveCodeTemplates(problem.getId(), problemDto.getCodeTemplates());

        // 为新的题目添加对应的case。
        problemCaseService.saveProblemCases(problem, problemDto.getSamples(), problemDto.getIsUploadTestCase(), problemDto.getUploadTestcaseDir());

        // 为新的题目添加对应的tag，可能tag是原表已有，也可能是新的，所以需要判断。
        problemTagService.saveOrUpdateProblemTags(problem.getId(), problemDto.getTags());
    }

    @Transactional
    @Override
    public void updateProblem(ProblemDTO problemDto) {
        Problem problem = problemDto.getProblem();
        AccountProfile profile = AccountUtils.getProfile();

        // 检查用户权限。
        ValidateUtils.validateProblemAndRole(problem, profile);

        // 检查ProblemId冲突。
        if(lambdaQuery().eq(Problem::getProblemId, problem.getProblemId().toUpperCase()).ne(Problem::getId, problem.getId()).oneOpt().isPresent()){
            throw new StatusFailException("该题目的Problem ID已存在，请更换！");
        }

        problem.setModifiedUser(profile.getUsername());

        // 更新 题目与语言 的关系。
        problemLanguageService.updateProblemLanguages(problem.getId(), problemDto.getLanguages());

        // 更新 代码模板。
        codeTemplateService.updateCodeTemplates(problem.getId(), problemDto.getCodeTemplates());

        // 更新 题目样例。
        problemCaseService.updateProblemCases(problem, problemDto.getSamples(), problemDto.getIsUploadTestCase(), problemDto.getUploadTestcaseDir());

        // 更新 题目与标签 的关系。
        problemTagService.updateProblemTags(problem.getId(), problemDto.getTags());

        // 更新题目。
        saveOrUpdateProblem(problem, problemDto.getSamples());

        //TODO judge update.
    }

    @Override
    public void changeProblemAuth(Problem problem) {
        ValidateUtils.validateProblemAuth(problem);
        AccountProfile profile = AccountUtils.getProfile();
        lambdaUpdate().eq(Problem::getId, problem.getId()).set(Problem::getAuth, problem.getAuth()).set(Problem::getModifiedUser, profile.getUsername()).update();
    }

    /**
     * 设置Problem其他属性，然后保存或更新。
     * @param problem
     */
    @Transactional
    public void saveOrUpdateProblem(Problem problem, List<ProblemCase> problemCases) {
        if (JudgeMode.DEFAULT.getMode().equals(problem.getJudgeMode())) {
            problem.setSpjLanguage(null).setSpjCode(null);
        }
        if (problem.getIsGroup() == null) {
            problem.setIsGroup(false);
        }
        problem.setProblemId(problem.getProblemId().toUpperCase());
        // 设置oi总分数，根据每个测试点的加和
        if (ProblemType.isOIType(problem.getType())) {
            int sumScore = ProblemUtils.calProblemTotalScore(problem.getJudgeCaseMode(), problemCases);
            problem.setIoScore(sumScore);
        }
        saveOrUpdate(problem);
    }

}
