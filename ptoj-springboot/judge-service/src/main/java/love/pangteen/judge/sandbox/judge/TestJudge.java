package love.pangteen.judge.sandbox.judge;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import love.pangteen.api.enums.JudgeStatus;
import love.pangteen.api.utils.FileUtils;
import love.pangteen.exception.JudgeSystemError;
import love.pangteen.judge.pojo.dto.JudgeDTO;
import love.pangteen.judge.pojo.dto.JudgeGlobalDTO;
import love.pangteen.judge.pojo.entity.LanguageConfig;
import love.pangteen.judge.pojo.entity.SandBoxResult;
import love.pangteen.judge.sandbox.SandboxManager;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/2/23 17:07
 **/
@Component
public class TestJudge extends AbstractJudge {

    @Override
    public JSONArray judgeCase(JudgeDTO judgeDTO, JudgeGlobalDTO judgeGlobalDTO) throws JudgeSystemError {
        LanguageConfig runConfig = judgeGlobalDTO.getRunConfig();
        // 调用安全沙箱使用测试点对程序进行测试
        return SandboxManager.testCase(
                parseRunCommand(runConfig.getRunCommand(), null, null, null),
                runConfig.getRunEnvs(),
                judgeDTO.getTestCaseInputPath(),
                judgeDTO.getTestCaseInputContent(),
                judgeGlobalDTO.getTestTime(),
                judgeGlobalDTO.getMaxMemory(),
                judgeDTO.getMaxOutputSize(),
                judgeGlobalDTO.getMaxStack(),
                runConfig.getExeName(),
                judgeGlobalDTO.getUserFileId(),
                judgeGlobalDTO.getUserFileContent(),
                judgeGlobalDTO.getIsFileIO(),
                judgeGlobalDTO.getIoReadFileName(),
                judgeGlobalDTO.getIoWriteFileName());
    }

    @Override
    public JSONObject checkResult(SandBoxResult sandBoxRes, JudgeDTO judgeDTO, JudgeGlobalDTO judgeGlobalDTO) throws JudgeSystemError {
        JSONObject result = new JSONObject();
        StringBuilder errMsg = new StringBuilder();
        // 如果测试跑题无异常
        if (sandBoxRes.getStatus().equals(JudgeStatus.STATUS_ACCEPTED.getStatus())) {
            // 对结果的时间损耗和空间损耗与题目限制做比较，判断是否mle和tle
            if (sandBoxRes.getTime() > judgeGlobalDTO.getMaxTime()) {
                result.set("status", JudgeStatus.STATUS_TIME_LIMIT_EXCEEDED.getStatus());
            } else if (sandBoxRes.getMemory() > judgeGlobalDTO.getMaxMemory() * 1024) {
                result.set("status", JudgeStatus.STATUS_MEMORY_LIMIT_EXCEEDED.getStatus());
            } else {
                if (judgeDTO.getTestCaseOutputContent() != null) {
                    if (judgeGlobalDTO.getRemoveEOLBlank() != null && judgeGlobalDTO.getRemoveEOLBlank()) {
                        String stdOut = FileUtils.rtrim(sandBoxRes.getStdout());
                        String testCaseOutput = FileUtils.rtrim(judgeDTO.getTestCaseOutputContent());
                        if (Objects.equals(stdOut, testCaseOutput)) {
                            result.set("status", JudgeStatus.STATUS_ACCEPTED.getStatus());
                        } else {
                            result.set("status", JudgeStatus.STATUS_WRONG_ANSWER.getStatus());
                        }
                    } else {
                        if (Objects.equals(sandBoxRes.getStdout(), judgeDTO.getTestCaseOutputContent())) {
                            result.set("status", JudgeStatus.STATUS_ACCEPTED.getStatus());
                        } else {
                            result.set("status", JudgeStatus.STATUS_WRONG_ANSWER.getStatus());
                        }
                    }
                } else {
                    result.set("status", JudgeStatus.STATUS_ACCEPTED.getStatus());
                }
            }
        } else if (sandBoxRes.getStatus().equals(JudgeStatus.STATUS_TIME_LIMIT_EXCEEDED.getStatus())) {
            result.set("status", JudgeStatus.STATUS_TIME_LIMIT_EXCEEDED.getStatus());
        } else if (sandBoxRes.getExitCode() != 0) {
            result.set("status", JudgeStatus.STATUS_RUNTIME_ERROR.getStatus());
            if (sandBoxRes.getExitCode() < 32) {
                errMsg.append(String.format("ExitCode: %s (%s)\n", sandBoxRes.getExitCode(), SandboxManager.getSignal(sandBoxRes.getExitCode().intValue())));
            } else {
                errMsg.append(String.format("ExitCode: %s\n", sandBoxRes.getExitCode()));
            }
        } else {
            result.set("status", sandBoxRes.getStatus());
            // 输出超限的特别提示
            if ("Output Limit Exceeded".equals(sandBoxRes.getOriginalStatus())) {
                errMsg.append("The output character length of the program exceeds the limit");
            }
        }

        // b
        result.set("memory", sandBoxRes.getMemory());
        // ns->ms
        result.set("time", sandBoxRes.getTime());

        if (!StrUtil.isEmpty(sandBoxRes.getStderr())) {
            errMsg.append(sandBoxRes.getStderr());
        }

        // 记录该测试点的错误信息
        if (!StrUtil.isEmpty(errMsg.toString())) {
            String str = errMsg.toString();
            result.set("errMsg", str.substring(0, Math.min(1024 * 1024, str.length())));
        }

        // 记录该测试点的运行输出
        if (!StrUtil.isEmpty(sandBoxRes.getStdout()) && sandBoxRes.getStdout().length() > 1000) {
            result.set("output", sandBoxRes.getStdout().substring(0, 1000) + "...");
        } else {
            result.set("output", sandBoxRes.getStdout());
        }
        return result;
    }
    
}
