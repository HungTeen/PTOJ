package love.pangteen.problem.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import love.pangteen.api.constant.OJConstant;
import love.pangteen.api.enums.JudgeStatus;
import love.pangteen.api.pojo.entity.Judge;
import love.pangteen.api.pojo.entity.TestJudgeResult;
import love.pangteen.api.pojo.vo.ProblemCountVO;
import love.pangteen.api.pojo.vo.ProblemVO;
import love.pangteen.api.utils.JudgeUtils;
import love.pangteen.exception.StatusFailException;
import love.pangteen.pojo.AccountProfile;
import love.pangteen.problem.mapper.JudgeMapper;
import love.pangteen.problem.mapper.ProblemMapper;
import love.pangteen.problem.pojo.dto.PidListDTO;
import love.pangteen.problem.pojo.dto.SubmitIdListDTO;
import love.pangteen.problem.pojo.vo.JudgeCaseVO;
import love.pangteen.problem.pojo.vo.JudgeVO;
import love.pangteen.problem.pojo.vo.TestJudgeVO;
import love.pangteen.problem.service.JudgeService;
import love.pangteen.problem.service.ProblemService;
import love.pangteen.utils.AccountUtils;
import love.pangteen.utils.RedisUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/25 16:11
 **/
@Service
public class JudgeServiceImpl extends ServiceImpl<JudgeMapper, Judge> implements JudgeService {

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private ProblemService problemService;

    @Resource
    private ProblemMapper problemMapper;

//    @Resource
//    private JudgeValidateUtils validateUtils;

    @Override
    public Page<ProblemVO> getProblemList(Integer limit, Integer currentPage, String keyword, List<Long> tagIds, Integer difficulty, String oj) {
        Page<ProblemVO> page = new Page<>(currentPage, limit);
        Integer tagListSize = null;
        if (CollUtil.isNotEmpty(tagIds)) {
            tagListSize = Math.toIntExact(tagIds.stream().distinct().count());
        }
        List<ProblemVO> problemList = problemMapper.getProblemList(page, null, keyword, difficulty, tagIds, tagListSize, oj);
        if(!problemList.isEmpty()){
            List<Long> pidList = problemList.stream().map(ProblemVO::getPid).collect(Collectors.toList());
            List<ProblemCountVO> problemListCount = getProblemListCount(pidList);
            for (ProblemVO problemVo : problemList) {
                for (ProblemCountVO problemCountVo : problemListCount) {
                    if (problemVo.getPid().equals(problemCountVo.getPid())) {
                        problemVo.setProblemCountVo(problemCountVo);
                        break;
                    }
                }
            }
        }
        return page.setRecords(problemList);
    }

    /**
     * 获取用户对应该题目列表中各个题目的做题情况。
     */
    @Override
    public HashMap<Long, Object> getUserProblemStatus(PidListDTO pidListDto) {
        AccountProfile profile = AccountUtils.getProfile();
        HashMap<Long, Object> result = new HashMap<>();

        // 先查询判断该用户对于这些题是否已经通过，若已通过，则无论后续再提交结果如何，该题都标记为通过
        List<Judge> judges = getSubmitJudges(pidListDto.getPidList(), profile.getUuid(), pidListDto.getIsContestProblemList() ? pidListDto.getCid() : 0, pidListDto.getGid());

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

    /**
     * 通用查询判题记录列表。
     * @param onlyMine 只查看当前用户的提交。
     */
    @Override
    public IPage<JudgeVO> getSubmissionList(Integer limit, Integer currentPage, Boolean onlyMine, String searchPid, Integer searchStatus, String searchUsername, Boolean completeProblemID, Long gid) {
        IPage<JudgeVO> judgeList = lambdaQuery()
                .eq(Judge::getCid, 0)
                .eq(Judge::getCpid, 0)
                .like(StrUtil.isNotEmpty(searchUsername), Judge::getUsername, searchUsername)
                .eq(searchStatus != null, Judge::getStatus, searchStatus)
                .eq(onlyMine, Judge::getUid, StpUtil.getLoginIdAsString())
                .eq(gid != null, Judge::getGid, gid)
                .and(StrUtil.isNotEmpty(searchPid), wrapper -> {
                    if(completeProblemID){
                        wrapper.eq(Judge::getDisplayPid, searchPid);
                    } else {
                        wrapper.like(Judge::getDisplayPid, searchPid);
                    }
                })
                .orderByDesc(Judge::getSubmitTime, Judge::getSubmitId)
                .page(new Page<>(currentPage, limit))
                .convert(judge -> {
                    JudgeVO judgeVO = new JudgeVO();
                    BeanUtils.copyProperties(judge, judgeVO);
                    return judgeVO;
                });
        List<JudgeVO> records = judgeList.getRecords();
        if(CollUtil.isNotEmpty(records)){
            List<Long> pidList = records.stream().map(JudgeVO::getPid).collect(Collectors.toList());
            Map<Long, String> problemTitleMap = problemService.getProblemTitleMap(pidList);
            records.forEach(judgeVO -> judgeVO.setTitle(problemTitleMap.get(judgeVO.getPid())));
        }
        return judgeList;
    }

    @Override
    public TestJudgeVO getTestJudgeResult(String testJudgeKey) {
        TestJudgeResult testJudgeRes = (TestJudgeResult) redisUtils.get(testJudgeKey);
        if (testJudgeRes == null) {
            throw new StatusFailException("查询错误！当前在线调试任务不存在！");
        }
        TestJudgeVO testJudgeVo = new TestJudgeVO();
        testJudgeVo.setStatus(testJudgeRes.getStatus());
        if (JudgeStatus.STATUS_PENDING.getStatus().equals(testJudgeRes.getStatus())) {
            return testJudgeVo;
        }
        testJudgeVo.setUserInput(testJudgeRes.getInput());
        testJudgeVo.setUserOutput(testJudgeRes.getStdout());
        testJudgeVo.setExpectedOutput(testJudgeRes.getExpectedOutput());
        testJudgeVo.setMemory(testJudgeRes.getMemory());
        testJudgeVo.setTime(testJudgeRes.getTime());
        testJudgeVo.setStderr(testJudgeRes.getStderr());
        testJudgeVo.setProblemJudgeMode(testJudgeRes.getProblemJudgeMode());
        redisUtils.del(testJudgeKey);
        return testJudgeVo;
    }

    @Override
    public HashMap<Long, Object> checkCommonJudgeResult(SubmitIdListDTO submitIdListDto) {
        return null;
    }

    @Override
    public HashMap<Long, Object> checkContestJudgeResult(SubmitIdListDTO submitIdListDto) {
        return null;
    }

    @Override
    public JudgeCaseVO getALLCaseResult(Long submitId) {
        return null;
    }

    @Override
    public List<Judge> getSubmitJudges(List<Long> pidList, String uid, Long cid, Long gid) {
        return lambdaQuery()
                .eq(Judge::getUid, uid)
                .eq(cid != null, Judge::getCid, cid)
                .eq(gid != null, Judge::getGid, gid)
                .isNull(gid == null, Judge::getGid)
                .in(Judge::getPid, pidList)
                .orderByDesc(Judge::getSubmitTime)
                .list();
    }

    @Override
    public List<ProblemCountVO> getProblemListCount(List<Long> pidList) {
        return getBaseMapper().getProblemListCount(pidList);
    }
}
