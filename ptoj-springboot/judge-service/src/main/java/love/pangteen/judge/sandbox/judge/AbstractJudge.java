package love.pangteen.judge.sandbox.judge;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import love.pangteen.api.constant.OJFiles;
import love.pangteen.api.utils.JudgeUtils;
import love.pangteen.exception.JudgeSystemError;
import love.pangteen.judge.pojo.dto.JudgeDTO;
import love.pangteen.judge.pojo.dto.JudgeGlobalDTO;
import love.pangteen.judge.pojo.entity.SandBoxResult;

import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/2/1 10:46
 **/
public abstract class AbstractJudge {

    protected static final int SPJ_PC = 99;

    protected static final int SPJ_AC = 100;

    protected static final int SPJ_PE = 101;

    protected static final int SPJ_WA = 102;

    protected static final int SPJ_ERROR = 103;

    public JSONObject judge(JudgeDTO judgeDTO, JudgeGlobalDTO judgeGlobalDTO) throws JudgeSystemError {

        JSONArray judgeResultList = judgeCase(judgeDTO, judgeGlobalDTO);

        switch (judgeGlobalDTO.getJudgeMode()) {
            case SPJ:
            case TEST:
            case DEFAULT:
                return process(judgeDTO, judgeGlobalDTO, judgeResultList);
//            case INTERACTIVE:
//                return processMultiple(judgeDTO, judgeGlobalDTO, judgeResultList);
            default:
                throw new RuntimeException("The problem mode is error:" + judgeGlobalDTO.getJudgeMode());
        }

    }

    private JSONObject process(JudgeDTO judgeDTO, JudgeGlobalDTO judgeGlobalDTO, JSONArray judgeResultList) throws JudgeSystemError {

        String stdoutName = BooleanUtil.isTrue(judgeGlobalDTO.getIsFileIO()) ? judgeGlobalDTO.getIoWriteFileName() : "stdout";
        JSONObject judgeResult = (JSONObject) judgeResultList.get(0);
        SandBoxResult sandBoxRes = SandBoxResult.builder()
                .stdout(((JSONObject) judgeResult.get("files")).getStr(stdoutName, ""))
                .stderr(((JSONObject) judgeResult.get("files")).getStr("stderr"))
                .time(judgeResult.getLong("time") / 1000000) //  ns->ms
                .memory(judgeResult.getLong("memory") / 1024) // b-->kb
                .exitCode(judgeResult.getLong("exitStatus"))
                .status(judgeResult.getInt("status"))
                .originalStatus(judgeResult.getStr("originalStatus"))
                .build();

        return checkResult(sandBoxRes, judgeDTO, judgeGlobalDTO);
    }

    public abstract JSONArray judgeCase(JudgeDTO judgeDTO, JudgeGlobalDTO judgeGlobalDTO) throws JudgeSystemError;

    public abstract JSONObject checkResult(SandBoxResult sandBoxRes, JudgeDTO judgeDTO, JudgeGlobalDTO judgeGlobalDTO) throws JudgeSystemError;

    protected static List<String> parseRunCommand(String command,
                                                  String testCaseInputName,
                                                  String userOutputName,
                                                  String testCaseOutputName) {

        if (testCaseInputName != null) {
            command = command.replace("{std_input}", OJFiles.tmpfsFolder(testCaseInputName));
        }

        if (userOutputName != null) {
            command = command.replace("{user_output}", OJFiles.tmpfsFolder(userOutputName));
        }

        if (userOutputName != null) {
            command = command.replace("{std_output}", OJFiles.tmpfsFolder(testCaseOutputName));
        }

        return JudgeUtils.translateCommandline(command);
    }

}
