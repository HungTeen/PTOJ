package love.pangteen.problem.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import love.pangteen.api.constant.OJConstant;
import love.pangteen.api.enums.JudgeMode;
import love.pangteen.api.enums.ProblemAuth;
import love.pangteen.api.enums.ProblemType;
import love.pangteen.api.enums.RemoteOJ;
import love.pangteen.api.pojo.entity.Problem;
import love.pangteen.api.pojo.entity.ProblemCase;
import love.pangteen.api.pojo.entity.Tag;
import love.pangteen.api.utils.RandomUtils;
import love.pangteen.exception.StatusFailException;
import love.pangteen.exception.StatusForbiddenException;
import love.pangteen.exception.StatusNotFoundException;
import love.pangteen.pojo.AccountProfile;
import love.pangteen.problem.mapper.ProblemMapper;
import love.pangteen.problem.pojo.dto.ProblemDTO;
import love.pangteen.api.pojo.entity.Language;
import love.pangteen.problem.pojo.vo.LastAcceptedCodeVO;
import love.pangteen.problem.pojo.vo.ProblemFullScreenListVO;
import love.pangteen.problem.pojo.vo.ProblemInfoVO;
import love.pangteen.problem.pojo.vo.RandomProblemVO;
import love.pangteen.problem.service.*;
import love.pangteen.problem.utils.ProblemUtils;
import love.pangteen.utils.AccountUtils;
import love.pangteen.utils.ValidateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        return lambdaQuery().eq(Problem::getId, pid).oneOpt().orElseThrow(() -> new StatusFailException("查询失败！"));
    }

    @Transactional
    @Override
    public boolean deleteProblem(Long pid) {
        removeById(pid);
        problemLanguageService.deleteProblemLanguages(pid);
        codeTemplateService.deleteCodeTemplates(pid);
        problemCaseService.deleteProblemCases(pid);
        problemTagService.deleteProblemTags(pid);

        return true;
    }

    @Transactional
    @Override
    public boolean addProblem(ProblemDTO problemDto) {
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

        return true;
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

    @Override
    public RandomProblemVO getRandomProblem() {
        List<Problem> problemList = lambdaQuery()
                .select(Problem::getProblemId)
                .eq(Problem::getAuth, ProblemAuth.PUBLIC.getAuth())
                .eq(Problem::getIsGroup, false)
                .list();
        if(problemList.isEmpty()){
            throw new StatusFailException("获取随机题目失败，题库暂无公开题目！");
        }
        int index = RandomUtils.get().nextInt(problemList.size());
        RandomProblemVO randomProblemVO = new RandomProblemVO();
        randomProblemVO.setProblemId(problemList.get(index).getProblemId());
        return randomProblemVO;
    }

    @Override
    public ProblemInfoVO getProblemInfo(String problemId, Long gid) {
        Problem problem = lambdaQuery().eq(Problem::getProblemId, problemId)
                .oneOpt().orElseThrow(() -> new StatusNotFoundException("该题号对应的题目不存在"));
        if (problem.getAuth() != 1) {
            throw new StatusForbiddenException("该题号对应题目并非公开题目，不支持访问！");
        }
        // Check Group.

        List<Tag> tags = problemTagService.getProblemTags(problem.getId());
        List<Language> languages = problemLanguageService.getLanguages(problem.getId());
        HashMap<String, String> langTemplateMap = codeTemplateService.getLangTemplateMap(problem.getId(), languages);
        //TODO 题目提交统计情况。
        return ProblemInfoVO.builder()
                .problem(problem)
                .tags(tags)
                .languages(languages.stream().map(Language::getName).collect(Collectors.toList()))
                .codeTemplate(langTemplateMap)
                .build();
    }

    @Override
    public LastAcceptedCodeVO getUserLastAcceptedCode(Long pid, Long cid) {
        return null;
    }

    @Override
    public List<ProblemFullScreenListVO> getFullScreenProblemList(Long tid, Long cid) {
        return null;
    }

    @Override
    public String getProblemOJ(Long pid) {
        String oj = OJConstant.DEFAULT_OJ;
        if (pid != null) {
            Problem problem = getById(pid);
            if (problem.getIsRemote()) {
                oj = problem.getProblemId().split("-")[0];
            }
            if (oj.equals(RemoteOJ.GYM.getName())) {  // GYM用与CF一样的编程语言列表
                oj = RemoteOJ.CODEFORCES.getName();
            }
        }
        return oj;
    }

    @Override
    public List<Long> getProblemsByCreateDate() {
        return getBaseMapper().getProblemsByCreateDate();
    }

    @Override
    public Map<Long, String> getProblemTitleMap(List<Long> pidList) {
        List<Problem> list = lambdaQuery()
                .select(Problem::getTitle, Problem::getId)
                .in(Problem::getId, pidList).list();
        return CollUtil.toMap(list, new HashMap<>(), Problem::getId, Problem::getTitle);
    }

    @Override
    public Problem getByProblemId(String problemId) {
        return lambdaQuery().eq(Problem::getProblemId, problemId).oneOpt().orElse(null);
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
