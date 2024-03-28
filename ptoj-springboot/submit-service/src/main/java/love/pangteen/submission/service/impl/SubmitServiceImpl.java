package love.pangteen.submission.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import love.pangteen.api.enums.JudgeStatus;
import love.pangteen.api.message.JudgeMessage;
import love.pangteen.api.message.SubmitMessage;
import love.pangteen.api.pojo.entity.Judge;
import love.pangteen.api.pojo.entity.Problem;
import love.pangteen.api.pojo.entity.TestJudgeResult;
import love.pangteen.api.service.IDubboProblemService;
import love.pangteen.api.utils.JudgeUtils;
import love.pangteen.api.utils.RoleUtils;
import love.pangteen.exception.StatusFailException;
import love.pangteen.exception.StatusNotFoundException;
import love.pangteen.pojo.AccountProfile;
import love.pangteen.submission.pojo.dto.SubmitJudgeDTO;
import love.pangteen.submission.pojo.dto.TestJudgeDTO;
import love.pangteen.submission.pojo.vo.SubmissionInfoVO;
import love.pangteen.submission.producer.RocketMQProducer;
import love.pangteen.submission.service.JudgeService;
import love.pangteen.submission.service.SubmitService;
import love.pangteen.submission.utils.SubmitUtils;
import love.pangteen.submission.utils.JudgeValidateUtils;
import love.pangteen.utils.AccountUtils;
import love.pangteen.utils.IpUtils;
import love.pangteen.utils.RedisUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/26 15:34
 **/
@Service
public class SubmitServiceImpl implements SubmitService {

    @DubboReference
    private IDubboProblemService problemService;

    @Resource
    private JudgeService judgeService;

    @Resource
    private JudgeValidateUtils judgeValidateUtils;

    @Resource
    private SubmitUtils submitUtils;

    @Resource
    private RocketMQProducer rocketMQProducer;

    @Resource
    private RedisUtils redisUtils;

    /**
     * 超级管理员与题目管理员有权限查看代码。<br>
     * 如果不是本人或者并未分享代码，则不可查看。<br>
     * 当此次提交代码不共享。
     * 比赛提交只有比赛创建者和root账号可看代码。<br>
     */
    @Override
    public SubmissionInfoVO getSubmission(Long submitId) {
        Judge judge = judgeService.getById(submitId);
        if (judge == null) throw new StatusNotFoundException("此提交数据不存在！");

//        boolean isRoot = StpUtil.hasRole(RoleUtils.getRoot());
        boolean self = judge.getUid().equals(StpUtil.getLoginIdAsString());

        // 清空vj信息。
        judge.setVjudgeUsername(null);
        judge.setVjudgeSubmitId(null);
        judge.setVjudgePassword(null);

        if (judge.getCid() != 0) {
            //TODO 比赛提交是否可见。
//            Contest contest = contestEntityService.getById(judge.getCid());
//            if (!isRoot && !userRolesVo.getUid().equals(contest.getUid())
//                    && !(judge.getGid() != null && groupValidator.isGroupRoot(userRolesVo.getUid(), judge.getGid()))) {
//                // 如果是比赛,那么还需要判断是否为封榜,比赛管理员和超级管理员可以有权限查看(ACM题目除外)
//                if (contest.getType().intValue() == Constants.Contest.TYPE_OI.getCode()
//                        && contestValidator.isSealRank(userRolesVo.getUid(), contest, true, false)) {
//                    submissionInfoVo.setSubmission(new Judge().setStatus(Constants.Judge.STATUS_SUBMITTED_UNKNOWN_RESULT.getStatus()));
//                    return submissionInfoVo;
//                }
//                // 不是本人的话不能查看代码、时间，空间，长度
//                if (!userRolesVo.getUid().equals(judge.getUid())) {
//                    judge.setCode(null);
//                    // 如果还在比赛时间，不是本人不能查看时间，空间，长度，错误提示信息
//                    if (contest.getStatus().intValue() == Constants.Contest.STATUS_RUNNING.getCode()) {
//                        judge.setTime(null);
//                        judge.setMemory(null);
//                        judge.setLength(null);
//                        judge.setErrorMessage("The contest is in progress. You are not allowed to view other people's error information.");
//                    }
//                }
//            }
//
//            // 团队比赛的提交代码 如果不是超管，需要检查网站是否开启隐藏代码功能
//            if (!isRoot && contest.getIsGroup() && judge.getCode() != null) {
//                try {
//                    accessValidator.validateAccess(HOJAccessEnum.HIDE_NON_CONTEST_SUBMISSION_CODE);
//                } catch (AccessException e) {
//                    judge.setCode("Because the super administrator has enabled " +
//                            "the function of not viewing the submitted code outside the contest of master station, \n" +
//                            "the code of this submission details has been hidden.");
//                }
//            }
        } else {
            boolean isProblemAdmin = StpUtil.hasRoleOr(RoleUtils.getProblemAdmins()); // 是否为题目管理员。

            if (!(judge.getShare() || isProblemAdmin || self)) {
                judge.setCode(null);
            }
//            if (!judge.getShare() && !isProblemAdmin && !(judge.getGid() != null && groupValidator.isGroupRoot(userRolesVo.getUid(), judge.getGid()))) {
//                    // 需要判断是否为当前登陆用户自己的提交代码
//                    if (!judge.getUid().equals(userRolesVo.getUid())) {
//                        judge.setCode(null);
//                    }
//            }
//            // 比赛外的提交代码 如果不是超管或题目管理员，需要检查网站是否开启隐藏代码功能
//            if (!isRoot && !isProblemAdmin && judge.getCode() != null) {
//                try {
//                    accessValidator.validateAccess(HOJAccessEnum.HIDE_NON_CONTEST_SUBMISSION_CODE);
//                } catch (AccessException e) {
//                    judge.setCode("Because the super administrator has enabled " +
//                            "the function of not viewing the submitted code outside the contest of master station, \n" +
//                            "the code of this submission details has been hidden.");
//                }
//            }
        }

        if (!JudgeUtils.canSeeErrorMsg(judge.getStatus())) {
            judge.setErrorMessage("The error message does not support viewing.");
        }

        SubmissionInfoVO submissionInfoVo = new SubmissionInfoVO();
        submissionInfoVo.setSubmission(judge);
        submissionInfoVo.setCodeShare(problemService.getById(judge.getPid()).getCodeShare());
        return submissionInfoVo;
    }

    /**
     * 判题通过Dubbo调用判题系统服务。
     */
    @Override
    public Judge submitProblemJudge(SubmitJudgeDTO judgeDto) {
        judgeValidateUtils.validateSubmission(judgeDto);

        //TODO 提交频率限制。
//        SwitchConfig switchConfig = nacosSwitchConfig.getSwitchConfig();
//        if (!isContestSubmission && switchConfig.getDefaultSubmitInterval() > 0) { // 非比赛提交有限制限制
//            String lockKey = Constants.Account.SUBMIT_NON_CONTEST_LOCK.getCode() + userRolesVo.getUid();
//            long count = redisUtils.incr(lockKey, 1);
//            if (count > 1) {
//                Long expireTime = redisUtils.getExpire(lockKey);
//                if (expireTime == null || expireTime == 0L){
//                    redisUtils.expire(lockKey, 3);
//                }
//                throw new StatusForbiddenException("对不起，您的提交频率过快，请稍后再尝试！");
//            }
//            redisUtils.expire(lockKey, switchConfig.getDefaultSubmitInterval());
//        }

        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        AccountProfile profile = AccountUtils.getProfile();
        Judge judge = new Judge();
        judge.setShare(false) // 默认设置代码为单独自己可见
                .setCode(judgeDto.getCode())
                .setCid(judgeDto.getCid())
                .setGid(judgeDto.getGid())
                .setLanguage(judgeDto.getLanguage())
                .setLength(judgeDto.getCode().length())
                .setUid(profile.getUuid())
                .setUsername(profile.getUsername())
                .setStatus(JudgeStatus.STATUS_PENDING.getStatus()) // 开始进入判题队列
                .setSubmitTime(new Date())
                .setVersion(0)
                .setIp(IpUtils.getUserIpAddr(request));

        boolean isContestSubmission = JudgeUtils.isContestSubmission(judgeDto.getCid());

        if (isContestSubmission) {

        } else if (JudgeUtils.isTrainingSubmission(judgeDto.getTid())) {

        } else {
            submitUtils.initCommonSubmission(judgeDto.getPid(), judgeDto.getGid(), judge);
        }

        judgeService.save(judge);

        // TODO 将提交加入消息队列。
        if (judgeDto.getIsRemote()) { // 如果是远程oj判题。
//            remoteJudgeDispatcher.sendJudgeTask(judge.getSubmitId(),
//                    judge.getPid(),
//                    judge.getDisplayPid(),
//                    isContestSubmission,
//                    false);
        } else {
            rocketMQProducer.sendJudgeTask(JudgeMessage.builder()
                    .isLocalTest(false)
                    .pid(judge.getPid())
                    .judgeId(judge.getSubmitId()).build());
            rocketMQProducer.sendSubmitTask(new SubmitMessage(judge.getPid(), profile.getUuid(), judge.getSubmitTime()));
        }
        return judge;
    }

    @Override
    public String submitProblemTestJudge(TestJudgeDTO testJudgeDto) {
        judgeValidateUtils.validateTestJudge(testJudgeDto);

        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        AccountProfile profile = AccountUtils.getProfile();

        // TODO 提交频率限制。
//        String lockKey = Constants.Account.TEST_JUDGE_LOCK.getCode() + userRolesVo.getUid();
//        long count = redisUtils.incr(lockKey, 1);
//        if (count > 1) {
//            Long expireTime = redisUtils.getExpire(lockKey);
//            if (expireTime == null || expireTime == 0L){
//                redisUtils.expire(lockKey, 3);
//            }
//            throw new StatusForbiddenException("对不起，您使用在线调试过于频繁，请稍后再尝试！");
//        }
//        redisUtils.expire(lockKey, 3);

        Problem problem = problemService.getById(testJudgeDto.getPid());
        if (problem == null) {
            throw new StatusFailException("当前题目不存在！不支持在线调试！");
        }

        String uniqueKey = "TEST_JUDGE_" + IdUtil.simpleUUID();
        rocketMQProducer.sendJudgeTask(JudgeMessage.builder()
                .isLocalTest(true)
                .pid(testJudgeDto.getPid())
                .code(testJudgeDto.getCode())
                .language(testJudgeDto.getLanguage())
                .uniqueKey(uniqueKey)
                .expectedOutput(testJudgeDto.getExpectedOutput())
                .userInput(testJudgeDto.getUserInput())
                .build());
        redisUtils.set(uniqueKey, TestJudgeResult.builder()
                .status(JudgeStatus.STATUS_PENDING.getStatus())
                .build(), 10 * 60);
        return uniqueKey;
    }

    @Override
    public Judge resubmit(Long submitId) {
        return null;
    }

    @Override
    public void updateSubmission(Judge judge) {

    }
}
