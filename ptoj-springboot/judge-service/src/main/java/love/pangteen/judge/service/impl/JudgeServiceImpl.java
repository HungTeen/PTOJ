package love.pangteen.judge.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import love.pangteen.api.constant.OJFiles;
import love.pangteen.api.enums.JudgeCaseMode;
import love.pangteen.api.enums.JudgeStatus;
import love.pangteen.api.pojo.dto.ToJudgeDTO;
import love.pangteen.api.pojo.entity.Judge;
import love.pangteen.api.pojo.entity.Problem;
import love.pangteen.api.service.IDubboProblemService;
import love.pangteen.api.utils.JudgeUtils;
import love.pangteen.exception.JudgeSystemError;
import love.pangteen.judge.config.properties.OJProperties;
import love.pangteen.judge.exception.CompileError;
import love.pangteen.judge.exception.SubmitError;
import love.pangteen.judge.manager.LanguageManager;
import love.pangteen.judge.mapper.JudgeMapper;
import love.pangteen.judge.pojo.entity.LanguageConfig;
import love.pangteen.judge.result.JudgeResult;
import love.pangteen.judge.sandbox.Compiler;
import love.pangteen.judge.sandbox.JudgeStrategy;
import love.pangteen.judge.sandbox.SandboxManager;
import love.pangteen.judge.service.JudgeService;
import love.pangteen.judge.utils.ProblemCaseUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/30 11:12
 **/
@Service
public class JudgeServiceImpl extends ServiceImpl<JudgeMapper, Judge> implements JudgeService {

    @Resource
    private OJProperties ojProperties;

    @DubboReference
    private IDubboProblemService problemService;

    @Resource
    private ProblemCaseUtils problemCaseUtils;

    /**
     * 标志该判题过程进入编译阶段。
     */
    @Override
    public void judge(Judge judge) {
        // 标志该判题过程进入编译阶段，写入当前判题服务的名字。
        boolean result = lambdaUpdate()
                .eq(Judge::getSubmitId, judge.getSubmitId())
                .ne(Judge::getStatus, JudgeStatus.STATUS_CANCELLED)
                .set(Judge::getJudger, ojProperties.getName())
                .set(Judge::getStatus, JudgeStatus.STATUS_COMPILING)
                .update();
        // 没更新成功，则可能表示该评测被取消 或者 judge记录被删除了，则结束评测。
        if(! result){
            // TODO Do something。
            return;
        }
        Problem problem = problemService.getById(judge.getPid());
        Judge finalJudge = postProblemJudge(problem, judge);
    }

    @Override
    public void remoteJudge(ToJudgeDTO toJudgeDTO) {

    }

    private Judge postProblemJudge(Problem problem, Judge judge) {
        // c和c++为一倍时间和空间，其它语言为2倍时间和空间。
        LanguageConfig languageConfig = LanguageManager.getLanguageConfigByName(judge.getLanguage());
        if (languageConfig.getSrcName() == null || (!languageConfig.getSrcName().endsWith(".c") && !languageConfig.getSrcName().endsWith(".cpp"))) {
            problem.setTimeLimit(problem.getTimeLimit() * 2);
            problem.setMemoryLimit(problem.getMemoryLimit() * 2);
        }

        HashMap<String, Object> judgeResult = judge(problem, judge);

        Judge finalJudgeRes = new Judge();
        finalJudgeRes.setSubmitId(judge.getSubmitId());
        // 如果是编译失败、提交错误或者系统错误就有错误提醒
        if (judgeResult.get("code") == Constants.Judge.STATUS_COMPILE_ERROR.getStatus() ||
                judgeResult.get("code") == Constants.Judge.STATUS_SYSTEM_ERROR.getStatus() ||
                judgeResult.get("code") == Constants.Judge.STATUS_RUNTIME_ERROR.getStatus() ||
                judgeResult.get("code") == Constants.Judge.STATUS_SUBMITTED_FAILED.getStatus()) {
            finalJudgeRes.setErrorMessage((String) judgeResult.getOrDefault("errMsg", ""));
        }
        // 设置最终结果状态码
        finalJudgeRes.setStatus((Integer) judgeResult.get("code"));
        // 设置最大时间和最大空间不超过题目限制时间和空间
        // kb
        Integer memory = (Integer) judgeResult.get("memory");
        finalJudgeRes.setMemory(Math.min(memory, problem.getMemoryLimit() * 1024));
        // ms
        Integer time = (Integer) judgeResult.get("time");
        finalJudgeRes.setTime(Math.min(time, problem.getTimeLimit()));
        // score
        finalJudgeRes.setScore((Integer) judgeResult.getOrDefault("score", null));
        // oi_rank_score
        finalJudgeRes.setOiRankScore((Integer) judgeResult.getOrDefault("oiRankScore", null));

        return finalJudgeRes;
    }

    public JudgeResult judge(Problem problem, Judge judge) {
        HashMap<String, Object> result = new HashMap<>();
        // 编译好的临时代码文件id。
        String userFileId = null;
        try {
            // 对用户源代码进行编译 获取tmpfs中的fileId。
            LanguageConfig languageConfig = LanguageManager.getLanguageConfigByName(judge.getLanguage());
            // 有的语言可能不支持编译, 目前有js、php不支持编译。
            if (languageConfig.getCompileCommand() != null) {
                userFileId = Compiler.compile(
                        languageConfig,
                        judge.getCode(),
                        judge.getLanguage(),
                        JudgeUtils.getProblemExtraFileMap(problem, "user")
                );
            }
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

            // 获取题目数据的评测模式
            String infoJudgeCaseMode = testCasesInfo.getStr("judgeCaseMode", JudgeCaseMode.DEFAULT.getMode());
            String judgeCaseMode = getFinalJudgeCaseMode(problem.getType(), problem.getJudgeCaseMode(), infoJudgeCaseMode);

            // 开始测试每个测试点
            List<JSONObject> allCaseResultList = judgeRun.judgeAllCase(judge.getSubmitId(),
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
            result.put("code", JudgeStatus.STATUS_SYSTEM_ERROR.getStatus());
            result.put("errMsg", "Oops, something has gone wrong with the judgeServer. Please report this to administrator.");
            result.put("time", 0);
            result.put("memory", 0);
            log.error("[Judge] [System Error] Submit Id:[{}] Problem Id:[{}], Error:[{}]",
                    judge.getSubmitId(),
                    problem.getId(),
                    systemError);
        } catch (SubmitError submitError) {
            result.put("code", Constants.Judge.STATUS_SUBMITTED_FAILED.getStatus());
            result.put("errMsg", mergeNonEmptyStrings(submitError.getMessage(), submitError.getStdout(), submitError.getStderr()));
            result.put("time", 0);
            result.put("memory", 0);
            log.error("[Judge] [Submit Error] Submit Id:[{}] Problem Id:[{}], Error:[{}]",
                    judge.getSubmitId(),
                    problem.getId(),
                    submitError);
        } catch (CompileError compileError) {
            result.put("code", JudgeStatus.STATUS_COMPILE_ERROR.getStatus());
            result.put("errMsg", mergeNonEmptyStrings(compileError.getStdout(), compileError.getStderr()));
            result.put("time", 0);
            result.put("memory", 0);
        } catch (Exception e) {
            result.put("code", JudgeStatus.STATUS_SYSTEM_ERROR.getStatus());
            result.put("errMsg", "Oops, something has gone wrong with the judgeServer. Please report this to administrator.");
            result.put("time", 0);
            result.put("memory", 0);
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
        return result;
    }
}
