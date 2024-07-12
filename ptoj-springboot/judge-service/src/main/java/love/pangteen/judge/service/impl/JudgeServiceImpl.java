package love.pangteen.judge.service.impl;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.micrometer.core.lang.Nullable;
import lombok.extern.slf4j.Slf4j;
import love.pangteen.api.constant.OJFiles;
import love.pangteen.api.enums.JudgeCaseMode;
import love.pangteen.api.enums.JudgeStatus;
import love.pangteen.api.enums.ProblemType;
import love.pangteen.api.pojo.dto.TestJudgeDTO;
import love.pangteen.api.pojo.dto.ToJudgeDTO;
import love.pangteen.api.pojo.entity.*;
import love.pangteen.api.service.IDubboProblemService;
import love.pangteen.api.service.IDubboUserService;
import love.pangteen.api.utils.JudgeUtils;
import love.pangteen.api.utils.RandomUtils;
import love.pangteen.api.utils.Utils;
import love.pangteen.exception.JudgeSystemError;
import love.pangteen.judge.config.properties.OJProperties;
import love.pangteen.judge.exception.CompileError;
import love.pangteen.judge.exception.SubmitError;
import love.pangteen.judge.manager.LanguageManager;
import love.pangteen.judge.mapper.JudgeMapper;
import love.pangteen.judge.pojo.entity.JudgeResult;
import love.pangteen.judge.pojo.entity.LanguageConfig;
import love.pangteen.judge.sandbox.Compiler;
import love.pangteen.judge.sandbox.JudgeRunner;
import love.pangteen.judge.sandbox.JudgeStrategy;
import love.pangteen.judge.sandbox.SandboxManager;
import love.pangteen.judge.service.JudgeCaseService;
import love.pangteen.judge.service.JudgeService;
import love.pangteen.judge.utils.ProblemCaseUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/30 11:12
 **/
@Slf4j
@Service
public class JudgeServiceImpl extends ServiceImpl<JudgeMapper, Judge> implements JudgeService {

    @Resource
    private OJProperties ojProperties;

    @DubboReference
    private IDubboProblemService problemService;

    @DubboReference
    private IDubboUserService userService;

    @Resource
    private ProblemCaseUtils problemCaseUtils;

    @Resource
    private JudgeRunner judgeRunner;

    @Resource
    private JudgeCaseService judgeCaseService;

    @Override
    public void randomInsertJudge() {
        Random random = RandomUtils.get();
        // Random Problem.
        List<Problem> problems = problemService.getAllProblems();
        Problem selectProblem = problems.get(random.nextInt(problems.size()));
        List<Language> allLanguage = problemService.getAllLanguage();
        Language selectLanguage = allLanguage.get(0);
        // Random User.
        List<UserInfo> allUsers = userService.getAllUsers();
        UserInfo selectUser = allUsers.get(random.nextInt(allUsers.size()));
        // Random Status.
        List<JudgeStatus> status = Arrays.stream(JudgeStatus.values()).filter(JudgeStatus::isValidStatus).collect(Collectors.toList());
        JudgeStatus judgeStatus = status.get(random.nextInt(status.size()));
        int runTime = random.nextInt(1000);
        int memory = random.nextInt(128);

        Judge judge = Judge.builder()
                .pid(selectProblem.getId())
                .displayPid(selectProblem.getProblemId())
                .uid(selectUser.getUuid())
                .username(selectUser.getUsername())
                .submitTime(Calendar.getInstance().getTime())
                .status(judgeStatus.getStatus())
                .errorMessage("Ignore error message")
                .time(runTime)
                .memory(memory)
                .length(666)
                .code("print 666")
                .language(selectLanguage.getName())
                .cid(0L)
                .cpid(0L)
                .judger("Dummy Judge")
                .isManual(true)
                .build();
        saveOrUpdate(judge);
    }

    @Override
    public void validateAllJudges() {
        lambdaUpdate()
                .isNull(Judge::getScore)
                .isNull(Judge::getIp)
                .and(wrapper -> wrapper.le(Judge::getStatus, -4)
                            .or().ge(Judge::getStatus, 5)
                )
                .set(Judge::getScore, 0)
                .set(Judge::getIp, "127.0.0.1")
                .set(Judge::getStatus, JudgeStatus.STATUS_ACCEPTED.getStatus())
                .update();
    }

    @Override
    public void updateJudgeStatus(JudgeStatus judgeStatus) {
        lambdaUpdate()
                .set(Judge::getStatus, judgeStatus.getStatus())
                .update();
    }

    /**
     * 标志该判题过程进入编译阶段。
     */
    @Transactional
    @Override
    public JudgeStatus judge(Judge judge) {
        // 标志该判题过程进入编译阶段，写入当前判题服务的名字。
        boolean result = lambdaUpdate()
                .eq(Judge::getSubmitId, judge.getSubmitId())
                .ne(Judge::getStatus, JudgeStatus.STATUS_CANCELLED.getStatus())
                .set(Judge::getJudger, ojProperties.getName())
                .set(Judge::getStatus, JudgeStatus.STATUS_COMPILING.getStatus())
                .update();
        // 没更新成功，则可能表示该评测被取消 或者 judge记录被删除了，则结束评测。
        if(! result){
            // TODO Do something。
            return JudgeStatus.STATUS_CANCELLED;
        }
        Problem problem = problemService.getById(judge.getPid());
        Judge finalJudge = postProblemJudge(problem, judge);
        updateById(finalJudge);

        if (!Objects.equals(finalJudge.getStatus(), JudgeStatus.STATUS_SUBMITTED_FAILED.getStatus())) {
            // 更新其它表
            updateOtherTable(finalJudge.getSubmitId(),
                    finalJudge.getStatus(),
                    judge.getCid(),
                    judge.getUid(),
                    judge.getPid(),
                    judge.getGid(),
                    finalJudge.getScore(),
                    finalJudge.getTime());
        }
        return JudgeStatus.getTypeByStatus(finalJudge.getStatus());
    }

    @Override
    public void remoteJudge(ToJudgeDTO toJudgeDTO) {

    }

    @Override
    public TestJudgeResult testJudge(TestJudgeDTO testJudgeDTO) {
        TestJudgeResult.TestJudgeResultBuilder builder = TestJudgeResult.builder();
        if(LanguageManager.doubleLanguageLimit(testJudgeDTO.getLanguage())){
            testJudgeDTO.setTimeLimit(testJudgeDTO.getTimeLimit() * 2);
            testJudgeDTO.setMemoryLimit(testJudgeDTO.getMemoryLimit() * 2);
        }

        // 编译好的临时代码文件id
        String userFileId = null;
        try {
            userFileId = compile(testJudgeDTO.getLanguage(), testJudgeDTO.getCode(), testJudgeDTO.getExtraFile());
            return judgeRunner.testJudgeCase(userFileId, testJudgeDTO);
        } catch (JudgeSystemError systemError) {
            log.error("[Test Judge] [System Error] [{}]", systemError.toString());
            return builder
                    .memory(0L)
                    .time(0L)
                    .status(JudgeStatus.STATUS_COMPILE_ERROR.getStatus())
                    .stderr("Oops, something has gone wrong with the judgeServer. Please report this to administrator.")
                    .build();
        } catch (SubmitError submitError) {
            log.error("[Test Judge] [Submit Error] [{}]", submitError.toString());
            return builder
                    .memory(0L)
                    .time(0L)
                    .status(JudgeStatus.STATUS_SUBMITTED_FAILED.getStatus())
                    .stderr(Utils.mergeNonEmptyStrings(submitError.getMessage(), submitError.getStdout(), submitError.getStderr()))
                    .build();
        } catch (CompileError compileError) {
            return builder
                    .memory(0L)
                    .time(0L)
                    .status(JudgeStatus.STATUS_COMPILE_ERROR.getStatus())
                    .stderr(Utils.mergeNonEmptyStrings(compileError.getStdout(), compileError.getStderr()))
                    .build();
        } catch (Exception e) {
            log.error("[Test Judge] [Error] [{}]", e.toString());
            return builder
                    .memory(0L)
                    .time(0L)
                    .status(JudgeStatus.STATUS_COMPILE_ERROR.getStatus())
                    .stderr("Oops, something has gone wrong with the judgeServer. Please report this to administrator.")
                    .build();
        } finally {
            // 删除tmpfs内存中的用户代码可执行文件
            if (!StrUtil.isEmpty(userFileId)) {
                SandboxManager.delFile(userFileId);
            }
        }
    }

    @Override
    public int getUserAcceptCount(String uid) {
        return getBaseMapper().getJudgeCount(uid, JudgeStatus.STATUS_ACCEPTED.getStatus());
    }

    @Transactional
    public Judge postProblemJudge(Problem problem, Judge judge) {
        if(LanguageManager.doubleLanguageLimit(judge.getLanguage())){
            problem.setTimeLimit(problem.getTimeLimit() * 2);
            problem.setMemoryLimit(problem.getMemoryLimit() * 2);
        }

        JudgeResult judgeResult = judge(problem, judge);

        Judge finalJudgeRes = new Judge();
        finalJudgeRes.setSubmitId(judge.getSubmitId());
        // 如果是编译失败、提交错误或者系统错误就有错误提醒
        if (JudgeUtils.canSeeErrorMsg(judgeResult.getCode())) {
            finalJudgeRes.setErrorMessage(judgeResult.getErrMsg());
        }
        // 设置最终结果状态码
        finalJudgeRes.setStatus(judgeResult.getCode());
        // 设置最大时间和最大空间不超过题目限制时间和空间
        // kb
        finalJudgeRes.setMemory(judgeResult.getMemory());
        // ms
        finalJudgeRes.setTime(judgeResult.getTime());
        // score
        finalJudgeRes.setScore(judgeResult.getScore());
        // oi_rank_score
        finalJudgeRes.setOiRankScore(judgeResult.getOiRankScore());

        return finalJudgeRes;
    }

    @Nullable
    public String compile(String language, String code, HashMap<String, String> extraFiles) throws JudgeSystemError, CompileError, SubmitError {
        // 对用户源代码进行编译 获取tmpfs中的fileId。
        LanguageConfig languageConfig = LanguageManager.getLanguageConfigByName(language);
        // 有的语言可能不支持编译, 目前有js、php不支持编译。
        if (languageConfig.getCompileCommand() != null) {
            return Compiler.compile(
                    languageConfig,
                    code,
                    language,
                    extraFiles
            );
        }
        return null;
    }

    @Transactional
    public JudgeResult judge(Problem problem, Judge judge) {
        JudgeResult.JudgeResultBuilder builder = JudgeResult.builder();
        // 编译好的临时代码文件id。
        String userFileId = null;
        try {
            userFileId = compile(judge.getLanguage(), judge.getCode(), JudgeUtils.getProblemExtraFileMap(problem, "user"));

            // 测试数据文件所在文件夹。
            String testCasesDir = OJFiles.getJudgeCaseFolder(problem.getId());
            // 从文件中加载测试数据json。
            JSONObject testCasesInfo = problemCaseUtils.loadTestCaseInfo(
                    problem.getId(),
                    testCasesDir,
                    problem.getCaseVersion(),
                    problem.getJudgeMode(),
                    problem.getJudgeCaseMode()
            );

            // 检查是否为spj或者interactive，同时是否有对应编译完成的文件，若不存在，就先编译生成该文件，同时也要检查版本。
            if (!JudgeStrategy.checkOrCompileExtraProgram(problem)) {
                return JudgeResult.builder()
                        .code(JudgeStatus.STATUS_SYSTEM_ERROR.getStatus())
                        .errMsg("The special judge or interactive program code does not exist.")
                        .time(0).memory(0)
                        .build();
            }

            // 更新状态为评测数据中。
            lambdaUpdate().set(Judge::getStatus, JudgeStatus.STATUS_JUDGING.getStatus())
                    .eq(Judge::getSubmitId, judge.getSubmitId())
                    .update();

            // 获取题目数据的评测模式。
            String infoJudgeCaseMode = testCasesInfo.getStr("judgeCaseMode", JudgeCaseMode.DEFAULT.getMode());
            String judgeCaseMode = getFinalJudgeCaseMode(problem.getType(), problem.getJudgeCaseMode(), infoJudgeCaseMode);

            // 开始测试每个测试点。
            List<JSONObject> allCaseResultList = judgeRunner.judgeAllCase(judge.getSubmitId(),
                    problem,
                    judge.getLanguage(),
                    testCasesDir,
                    testCasesInfo,
                    userFileId,
                    judge.getCode(),
                    false,
                    judgeCaseMode);

            // 对全部测试点结果进行评判,获取最终评判结果
            return getJudgeInfo(allCaseResultList, problem, judge, judgeCaseMode);
        } catch (JudgeSystemError systemError) {
            builder.code(JudgeStatus.STATUS_SYSTEM_ERROR.getStatus())
                    .errMsg("Oops, something has gone wrong with the judgeServer. Please report this to administrator.")
                    .time(0).memory(0);
            log.error("[Judge] [System Error] Submit Id:[{}] Problem Id:[{}], Error:[{}]",
                    judge.getSubmitId(),
                    problem.getId(),
                    systemError);
        } catch (SubmitError submitError) {
            builder.code(JudgeStatus.STATUS_SUBMITTED_FAILED.getStatus())
                    .errMsg(Utils.mergeNonEmptyStrings(submitError.getMessage(), submitError.getStdout(), submitError.getStderr()))
                    .time(0).memory(0);
            log.error("[Judge] [Submit Error] Submit Id:[{}] Problem Id:[{}], Error:[{}]",
                    judge.getSubmitId(),
                    problem.getId(),
                    submitError);
        } catch (CompileError compileError) {
            builder.code(JudgeStatus.STATUS_COMPILE_ERROR.getStatus())
                    .errMsg(Utils.mergeNonEmptyStrings(compileError.getStdout(), compileError.getStderr()))
                    .time(0).memory(0);
        } catch (Exception e) {
            builder.code(JudgeStatus.STATUS_SYSTEM_ERROR.getStatus())
                    .errMsg("Oops, something has gone wrong with the judgeServer. Please report this to administrator.")
                    .time(0).memory(0);
            log.error("[Judge] [System Runtime Error] Submit Id:[{}] Problem Id:[{}], Error:[{}]",
                    judge.getSubmitId(),
                    problem.getId(),
                    e);
        } finally {

            // 删除tmpfs内存中的用户代码可执行文件
            if (!StrUtil.isEmpty(userFileId)) {
                SandboxManager.delFile(userFileId);
            }
        }
        return builder.build();
    }

    @Transactional
    public void updateOtherTable(Long submitId,
                                 Integer status,
                                 Long cid,
                                 String uid,
                                 Long pid,
                                 Long gid,
                                 Integer score,
                                 Integer useTime) {

        if (cid == 0) { // 非比赛提交
            // 如果是AC,就更新user_acproblem表,
//            if (status.intValue() == JudgeStatus.STATUS_ACCEPTED.getStatus() && gid == null) {
//                userAcproblemEntityService.saveOrUpdate(new UserAcproblem()
//                        .setPid(pid)
//                        .setUid(uid)
//                        .setSubmitId(submitId)
//                );
//            }
        } else { //如果是比赛提交
//            contestRecordEntityService.updateContestRecord(score, status, submitId, useTime);
        }
    }

    // 进行最终测试结果的判断（除编译失败外的评测状态码和时间，空间,OI题目的得分）
    public JudgeResult getJudgeInfo(List<JSONObject> testCaseResultList,
                                                Problem problem,
                                                Judge judge,
                                                String judgeCaseMode) {

        boolean isACM = ProblemType.isACMType(problem.getType());

        List<JSONObject> errorTestCaseList = new LinkedList<>();

        List<JudgeCase> allCaseResList = new LinkedList<>();

        // 记录所有测试点的结果
        testCaseResultList.forEach(jsonObject -> {
            Integer time = jsonObject.getLong("time").intValue();
            Integer memory = jsonObject.getLong("memory").intValue();
            Integer status = jsonObject.getInt("status");

            Long caseId = jsonObject.getLong("caseId", null);
            Integer groupNum = jsonObject.getInt("groupNum", null);
            Integer seq = jsonObject.getInt("seq", 0);
            String inputFileName = jsonObject.getStr("inputFileName");
            String outputFileName = jsonObject.getStr("outputFileName");
            String msg = jsonObject.getStr("errMsg");
            JudgeCase judgeCase = new JudgeCase();
            judgeCase.setTime(time)
                    .setMemory(memory)
                    .setStatus(status)
                    .setInputData(inputFileName)
                    .setOutputData(outputFileName)
                    .setPid(problem.getId())
                    .setUid(judge.getUid())
                    .setCaseId(caseId)
                    .setSeq(seq)
                    .setGroupNum(groupNum)
                    .setMode(judgeCaseMode)
                    .setSubmitId(judge.getSubmitId());

            if (!StrUtil.isEmpty(msg) && !Objects.equals(status, JudgeStatus.STATUS_COMPILE_ERROR.getStatus())) {
                judgeCase.setUserOutput(msg);
            }

            if (isACM) {
                if (!Objects.equals(status, JudgeStatus.STATUS_ACCEPTED.getStatus())) {
                    errorTestCaseList.add(jsonObject);
                }
            } else {
                int oiScore = jsonObject.getInt("score");
                if (Objects.equals(status, JudgeStatus.STATUS_ACCEPTED.getStatus())) {
                    judgeCase.setScore(oiScore);
                } else if (Objects.equals(status, JudgeStatus.STATUS_PARTIAL_ACCEPTED.getStatus())) {
                    errorTestCaseList.add(jsonObject);
                    Double percentage = jsonObject.getDouble("percentage");
                    if (percentage != null) {
                        int score = (int) Math.floor(percentage * oiScore);
                        judgeCase.setScore(score);
                    } else {
                        judgeCase.setScore(0);
                    }
                } else {
                    errorTestCaseList.add(jsonObject);
                    judgeCase.setScore(0);
                }
            }

            allCaseResList.add(judgeCase);
        });

        // 更新到数据库
        boolean addCaseRes = judgeCaseService.saveBatch(allCaseResList);
        if (!addCaseRes) {
            log.error("题号为：" + problem.getId() + "，提交id为：" + judge.getSubmitId() + "的各个测试数据点的结果更新到数据库操作失败");
        }

        // 获取判题的运行时间，运行空间，OI得分
        JudgeResult result = computeResultInfo(allCaseResList,
                isACM,
                errorTestCaseList.size(),
                problem.getIoScore(),
                problem.getDifficulty(),
                judgeCaseMode);

        // 如果该题为ACM类型的题目，多个测试点全部正确则AC，否则取第一个错误的测试点的状态
        // 如果该题为OI类型的题目, 若多个测试点全部正确则AC，若全部错误则取第一个错误测试点状态，否则为部分正确
        if (errorTestCaseList.isEmpty()) { // 全部测试点正确，则为AC
            result.setCode(JudgeStatus.STATUS_ACCEPTED.getStatus());
        } else if (isACM || errorTestCaseList.size() == testCaseResultList.size()) {
            errorTestCaseList.sort(Comparator.comparingInt(jsonObject -> jsonObject.getInt("seq")));
            result.setCode(errorTestCaseList.get(0).getInt("status"));
            result.setErrMsg(errorTestCaseList.get(0).getStr("errMsg", ""));
        } else {
            result.setCode(JudgeStatus.STATUS_PARTIAL_ACCEPTED.getStatus());
        }
        return result;
    }

    // 获取判题的运行时间，运行空间，OI得分
    public JudgeResult computeResultInfo(List<JudgeCase> allTestCaseResultList,
                                                     Boolean isACM,
                                                     Integer errorCaseNum,
                                                     Integer totalScore,
                                                     Integer problemDifficulty,
                                                     String judgeCaseMode) {
        JudgeResult.JudgeResultBuilder builder = JudgeResult.builder();
        // 用时和内存占用保存为多个测试点中最长的
        allTestCaseResultList.stream().max(Comparator.comparing(JudgeCase::getTime))
                .ifPresent(t -> builder.time(t.getTime()));

        allTestCaseResultList.stream().max(Comparator.comparing(JudgeCase::getMemory))
                .ifPresent(t -> builder.memory(t.getMemory()));

        // OI题目计算得分
        if (!isACM) {
            // 全对的直接用总分*0.1+2*题目难度
            if (errorCaseNum == 0 && JudgeCaseMode.DEFAULT.getMode().equals(judgeCaseMode)) {
                int oiRankScore = (int) Math.round(totalScore * 0.1 + 2 * problemDifficulty);
                builder.score(totalScore);
                builder.oiRankScore(oiRankScore);
            } else {
                int sumScore = 0;
                if (JudgeCaseMode.SUBTASK_LOWEST.getMode().equals(judgeCaseMode)) {
                    HashMap<Integer, Integer> groupNumMapScore = new HashMap<>();
                    for (JudgeCase testcaseResult : allTestCaseResultList) {
                        groupNumMapScore.merge(testcaseResult.getGroupNum(), testcaseResult.getScore(), Math::min);
                    }
                    for (Integer minScore : groupNumMapScore.values()) {
                        sumScore += minScore;
                    }
                } else if (JudgeCaseMode.SUBTASK_AVERAGE.getMode().equals(judgeCaseMode)) {
                    // 预处理 切换成Map Key: groupNum Value: <count,sum_score>
                    HashMap<Integer, Pair<Integer, Integer>> groupNumMapScore = new HashMap<>();
                    for (JudgeCase testcaseResult : allTestCaseResultList) {
                        Pair<Integer, Integer> pair = groupNumMapScore.get(testcaseResult.getGroupNum());
                        if (pair == null) {
                            groupNumMapScore.put(testcaseResult.getGroupNum(), new Pair<>(1, testcaseResult.getScore()));
                        } else {
                            int count = pair.getKey() + 1;
                            int score = pair.getValue() + testcaseResult.getScore();
                            groupNumMapScore.put(testcaseResult.getGroupNum(), new Pair<>(count, score));
                        }
                    }
                    for (Pair<Integer, Integer> pair : groupNumMapScore.values()) {
                        sumScore += (int) Math.round(pair.getValue() * 1.0 / pair.getKey());
                    }
                } else {
                    for (JudgeCase testcaseResult : allTestCaseResultList) {
                        sumScore += testcaseResult.getScore();
                    }
                }
                if (totalScore != 0 && sumScore > totalScore) {
                    sumScore = totalScore;
                }
                //测试点总得分*0.1+2*题目难度*（测试点总得分/题目总分）
                int oiRankScore = (int) Math.round(sumScore * 0.1 + 2 * problemDifficulty * (sumScore * 1.0 / totalScore));
                builder.score(sumScore);
                builder.oiRankScore(oiRankScore);
            }
        }
        return builder.build();
    }

    private String getFinalJudgeCaseMode(Integer type, String problemJudgeCaseMode, String infoJudgeCaseMode) {
        if (problemJudgeCaseMode == null) {
            return infoJudgeCaseMode;
        }
        if (ProblemType.isACMType(type)) {
            // ACM题目 以题目现有的judgeCaseMode为准
            return problemJudgeCaseMode;
        } else {
            // OI题目 涉及到可能有子任务分组评测，还是依赖info文件的配置为准
            return infoJudgeCaseMode;
        }
    }

}
