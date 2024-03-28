package love.pangteen.problem.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import love.pangteen.api.constant.OJConstant;
import love.pangteen.api.enums.*;
import love.pangteen.api.pojo.entity.Judge;
import love.pangteen.api.pojo.entity.Problem;
import love.pangteen.api.pojo.entity.ProblemCase;
import love.pangteen.api.pojo.vo.ProblemVO;
import love.pangteen.api.service.IDubboJudgeService;
import love.pangteen.api.utils.JudgeUtils;
import love.pangteen.api.utils.RandomUtils;
import love.pangteen.exception.StatusFailException;
import love.pangteen.exception.StatusForbiddenException;
import love.pangteen.exception.StatusNotFoundException;
import love.pangteen.pojo.AccountProfile;
import love.pangteen.problem.mapper.ProblemMapper;
import love.pangteen.problem.pojo.dto.PidListDTO;
import love.pangteen.problem.pojo.dto.ProblemDTO;
import love.pangteen.problem.pojo.entity.Language;
import love.pangteen.api.pojo.entity.Tag;
import love.pangteen.problem.pojo.vo.*;
import love.pangteen.problem.service.*;
import love.pangteen.problem.utils.ProblemUtils;
import love.pangteen.utils.ValidateUtils;
import love.pangteen.utils.AccountUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
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

    @Resource
    private ProblemMapper problemMapper;

    @DubboReference(check = false)
    private IDubboJudgeService judgeService;

    @Override
    public Page<ProblemVO> getProblemList(Integer limit, Integer currentPage, String keyword, List<Long> tagIds, Integer difficulty, String oj) {
        Page<ProblemVO> page = new Page<>(currentPage, limit);
        Integer tagListSize = null;
        if (CollUtil.isNotEmpty(tagIds)) {
            tagListSize = Math.toIntExact(tagIds.stream().distinct().count());
        }
        List<ProblemVO> problemList = problemMapper.getProblemList(page, null, keyword, difficulty, tagIds, tagListSize, oj);
        if(!problemList.isEmpty()){
//            List<Long> pidList = problemList.stream().map(ProblemVO::getPid).collect(Collectors.toList());
//            List<ProblemCountVO> problemListCount = judgeEntityService.getProblemListCount(pidList);
//            for (ProblemVO problemVo : problemList) {
//                for (ProblemCountVO problemCountVo : problemListCount) {
//                    if (problemVo.getPid().equals(problemCountVo.getPid())) {
//                        problemVo.setProblemCountVo(problemCountVo);
//                        break;
//                    }
//                }
//            }
        }
        return page.setRecords(problemList);
    }

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

    /**
     * 获取用户对应该题目列表中各个题目的做题情况。
     */
    @Override
    public HashMap<Long, Object> getUserProblemStatus(PidListDTO pidListDto) {
        AccountProfile profile = AccountUtils.getProfile();
        HashMap<Long, Object> result = new HashMap<>();

        // 先查询判断该用户对于这些题是否已经通过，若已通过，则无论后续再提交结果如何，该题都标记为通过
        List<Judge> judges = judgeService.getSubmitJudges(pidListDto.getPidList(), profile.getUuid(), pidListDto.getIsContestProblemList() ? pidListDto.getCid() : 0, pidListDto.getGid());

        boolean isACMContest = true;
        boolean isContainsContestEndJudge = false;
        long contestEndTime = 0L;
//        Contest contest = null; TODO 对于Contest的判断
        if (pidListDto.getIsContestProblemList()) {
//            contest = contestEntityService.getById(pidListDto.getCid());
//            if (contest == null) {
//                throw new StatusNotFoundException("错误：该比赛不存在！");
//            }
//            isACMContest = contest.getType().intValue() == Constants.Contest.TYPE_ACM.getCode();
//            isContainsContestEndJudge = Objects.equals(contest.getAllowEndSubmit(), true)
//                    && Objects.equals(pidListDto.getContainsEnd(), true);
//            contestEndTime = contest.getEndTime().getTime();
        }
//        boolean isSealRank = false;
//        if (!isACMContest && CollectionUtil.isNotEmpty(judges)) {
//            isSealRank = contestValidator.isSealRank(profile.getUid(), contest, false,
//                    SecurityUtils.getSubject().hasRole("root"));
//        }
        for (Judge judge : judges) {

            HashMap<String, Object> temp = new HashMap<>();
            if (pidListDto.getIsContestProblemList()) { // 如果是比赛的题目列表状态

                // 如果是隐藏比赛后的提交，需要判断提交时间进行过滤
//                if (!isContainsContestEndJudge && judge.getSubmitTime().getTime() >= contestEndTime) {
//                    continue;
//                }
//
//                if (!isACMContest) {
//                    if (!result.containsKey(judge.getPid())) {
//                        // IO比赛的，如果还未写入，则使用最新一次提交的结果
//                        // 判断该提交是否为封榜之后的提交,OI赛制封榜后的提交看不到提交结果，
//                        // 只有比赛结束可以看到,比赛管理员与超级管理员的提交除外
//                        if (isSealRank) {
//                            temp.put("status", Constants.Judge.STATUS_SUBMITTED_UNKNOWN_RESULT.getStatus());
//                            temp.put("score", null);
//                        } else {
//                            temp.put("status", judge.getStatus());
//                            temp.put("score", judge.getScore());
//                        }
//                        result.put(judge.getPid(), temp);
//                    }
//                } else {
//                    if (judge.getStatus().intValue() == Constants.Judge.STATUS_ACCEPTED.getStatus()) {
//                        // 如果该题目已通过，且同时是为不封榜前提交的，则强制写为通过（0）
//                        temp.put("status", Constants.Judge.STATUS_ACCEPTED.getStatus());
//                        temp.put("score", null);
//                        result.put(judge.getPid(), temp);
//                    } else if (!result.containsKey(judge.getPid())) {
//                        // 还未写入，则使用最新一次提交的结果
//                        temp.put("status", judge.getStatus());
//                        temp.put("score", null);
//                        result.put(judge.getPid(), temp);
//                    }
//                }

            } else { // 不是比赛题目
                if (JudgeUtils.isAccepted(judge.getStatus())) {
                    // 如果该题目已通过，则强制写为通过（0）
                    temp.put(OJConstant.JUDGE_STATUS, JudgeStatus.STATUS_ACCEPTED.getStatus());
                    result.put(judge.getPid(), temp);
                } else if (!result.containsKey(judge.getPid())) {
                    // 还未写入，则使用最新一次提交的结果
                    temp.put("status", judge.getStatus());
                    result.put(judge.getPid(), temp);
                }
            }
        }

        // 再次检查，应该可能从未提交过该题，则状态写为-10
        for (Long pid : pidListDto.getPidList()) {
            // 如果是比赛的题目列表状态
            if (pidListDto.getIsContestProblemList()) {
//                if (!result.containsKey(pid)) {
//                    HashMap<String, Object> temp = new HashMap<>();
//                    temp.put("score", null);
//                    temp.put("status", Constants.Judge.STATUS_NOT_SUBMITTED.getStatus());
//                    result.put(pid, temp);
//                }
            } else {
                if (!result.containsKey(pid)) {
                    HashMap<String, Object> temp = new HashMap<>();
                    temp.put(OJConstant.JUDGE_STATUS, JudgeStatus.STATUS_NOT_SUBMITTED.getStatus());
                    result.put(pid, temp);
                }
            }
        }
        return result;
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
