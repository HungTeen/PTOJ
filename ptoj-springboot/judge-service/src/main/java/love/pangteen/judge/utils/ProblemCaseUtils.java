package love.pangteen.judge.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import love.pangteen.api.constant.OJFiles;
import love.pangteen.api.enums.JudgeCaseMode;
import love.pangteen.api.enums.JudgeMode;
import love.pangteen.api.pojo.entity.ProblemCase;
import love.pangteen.api.service.IDubboProblemService;
import love.pangteen.api.utils.FileUtils;
import love.pangteen.exception.JudgeSystemError;
import love.pangteen.judge.pojo.dto.TestCaseDTO;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/31 16:19
 **/
@Component
public class ProblemCaseUtils {

    @DubboReference
    private IDubboProblemService dubboProblemService;

    /**
     * 获取指定题目的info数据。
     *
     * @param problemId     题目ID
     * @param testCasesDir  测试数据文件夹
     * @param version       样例版本号
     * @param judgeMode     判题模式。
     * @param judgeCaseMode 判题样例模式。
     */
    public JSONObject loadTestCaseInfo(Long problemId, String testCasesDir, String version, String judgeMode, String judgeCaseMode) throws JudgeSystemError {
        String infoFilePath = getInfoFilePath(testCasesDir);
        if (FileUtil.exist(infoFilePath)) {
            FileReader fileReader = new FileReader(infoFilePath, CharsetUtil.UTF_8);
            String infoStr = fileReader.readString();
            JSONObject testcaseInfo = JSONUtil.parseObj(infoStr);
            // 测试样例被改动需要重新生成。
            if (!testcaseInfo.getStr("version", "").equals(version)) {
                return tryInitTestCaseInfo(testCasesDir, problemId, version, judgeMode, judgeCaseMode);
            }
            return testcaseInfo;
        } else {
            return tryInitTestCaseInfo(testCasesDir, problemId, version, judgeMode, judgeCaseMode);
        }
    }

    /**
     * 若没有测试数据，则尝试从数据库获取并且初始化到本地，如果数据库中该题目测试数据为空，rsync同步也出了问题，则直接判系统错误。
     */
    public JSONObject tryInitTestCaseInfo(String testCasesDir,
                                          Long problemId,
                                          String version,
                                          String judgeMode,
                                          String judgeCaseMode) throws JudgeSystemError {

        List<ProblemCase> problemCases = dubboProblemService.getProblemCases(problemId);
        if (problemCases.isEmpty()) { // 数据库也为空的话。
            throw new JudgeSystemError("problemID:[" + problemId + "] test case has not found.", null, null);
        }

        if (StrUtil.isEmpty(judgeCaseMode)) {
            judgeCaseMode = JudgeCaseMode.DEFAULT.getMode();
        }

        JSONObject result = new JSONObject();
        result.set("mode", judgeMode);
        result.set("version", version);
        result.set("judgeCaseMode", judgeCaseMode);

        // 可能是zip上传记录的是文件名。
        ProblemCase one = problemCases.get(0);
        if (StrUtil.isEmpty(one.getInput())
                || StrUtil.isEmpty(one.getOutput())
                || (one.getInput().endsWith(".in") && (one.getOutput().endsWith(".out") || one.getOutput().endsWith(".ans")))
                || (one.getInput().endsWith(".txt") && one.getOutput().endsWith(".txt"))) {
            if (FileUtil.isEmpty(new File(testCasesDir))) { //如果本地对应文件夹也为空，说明文件丢失了
                throw new JudgeSystemError("problemID:[" + problemId + "] test case has not found.", null, null);
            } else {
                initLocalTestCase(result, judgeMode, judgeCaseMode, testCasesDir, problemCases);
            }
        } else {
            List<TestCaseDTO> testCases = problemCases.stream()
                    .map(problemCase -> TestCaseDTO.builder()
                            .input(problemCase.getInput())
                            .output(problemCase.getOutput())
                            .caseId(problemCase.getId())
                            .score(problemCase.getScore())
                            .groupNum(problemCase.getGroupNum())
                            .build()
                    ).collect(Collectors.toList());

            initTestCase(result, testCases, problemId, judgeMode, judgeCaseMode);
        }

        FileWriter infoFile = new FileWriter(getInfoFilePath(testCasesDir), CharsetUtil.UTF_8);
        // 写入记录文件
        infoFile.write(JSONUtil.toJsonStr(result));
        return result;
    }

    /**
     * 本地无文件初始化测试数据，写成json文件。
     */
    private void initTestCase(JSONObject result, List<TestCaseDTO> testCases,
                                   Long problemId,
                                   String judgeMode,
                                   String judgeCaseMode) throws JudgeSystemError {
        if (CollUtil.isEmpty(testCases)) {
            throw new JudgeSystemError("题号为：" + problemId + "的评测数据为空！", null, "The test cases does not exist.");
        }

        result.set("testCasesSize", testCases.size());

        JSONArray testCaseList = new JSONArray(testCases.size());

        String testCasesDir = OJFiles.getJudgeCaseFolder(problemId);

        // 无论有没有测试数据，一旦执行该函数，一律清空，重新生成该题目对应的测试数据文件。
        FileUtil.del(testCasesDir);

        for (int index = 0; index < testCases.size(); index++) {
            JSONObject jsonObject = new JSONObject();
            TestCaseDTO testCase = testCases.get(index);

            jsonObject.set("caseId", testCase.getCaseId());
            if (JudgeCaseMode.hasGroup(judgeCaseMode)) {
                jsonObject.set("groupNum", testCase.getGroupNum());
            }
            jsonObject.set("score", testCase.getScore());

            // 生成输入对应文件
            String inputName = (index + 1) + ".in";
            jsonObject.set("inputName", inputName);
            FileWriter inFileWriter = new FileWriter(OJFiles.join(testCasesDir, inputName), CharsetUtil.UTF_8);
            // 将该测试数据的输入写入到文件
            inFileWriter.write(testCase.getInput());

            // 生成输出对应文件
            String outputName = (index + 1) + ".out";
            jsonObject.set("outputName", outputName);
            FileWriter outFileWriter = new FileWriter(OJFiles.join(testCasesDir, outputName), CharsetUtil.UTF_8);
            outFileWriter.write(testCase.getOutput());

            commonHandle(jsonObject, judgeMode, testCase.getOutput());

            testCaseList.add(jsonObject);
        }

        result.set("testCases", testCaseList);
    }

    /**
     * 本地有文件，进行数据初始化 生成json文件。
     */
    private void initLocalTestCase(JSONObject result, String judgeMode,
                                        String judgeCaseMode,
                                        String testCasesDir,
                                        List<ProblemCase> problemCaseList) {
        result.set("testCasesSize", problemCaseList.size());
        result.set("testCases", new JSONArray());

        for (ProblemCase problemCase : problemCaseList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.set("caseId", problemCase.getId());
            if (JudgeCaseMode.hasGroup(judgeCaseMode)) {
                jsonObject.set("groupNum", problemCase.getGroupNum());
            }
            jsonObject.set("score", problemCase.getScore());
            jsonObject.set("inputName", problemCase.getInput());
            jsonObject.set("outputName", problemCase.getOutput());

            // 读取输出文件
            String output = "";
            String outputFilePath = testCasesDir + File.separator + problemCase.getOutput();
            if (FileUtil.exist(outputFilePath)) {
                FileReader outputFile = new FileReader(outputFilePath, CharsetUtil.UTF_8);
                output = outputFile.readString()
                        .replaceAll("\r\n", "\n") // 避免window系统的换行问题
                        .replaceAll("\r", "\n"); // 避免mac系统的换行问题
                FileWriter outFileWriter = new FileWriter(testCasesDir + File.separator + problemCase.getOutput(), CharsetUtil.UTF_8);
                outFileWriter.write(output);
            } else {
                FileWriter fileWriter = new FileWriter(outputFilePath);
                fileWriter.write("");
            }

            commonHandle(jsonObject, judgeMode, output);

            ((JSONArray) result.get("testCases")).put(jsonObject);
        }
    }

    private void commonHandle(JSONObject caseObject, String judgeMode, String outputData){
        // spj或interactive是根据特判程序输出判断结果，所以无需初始化测试数据
        if (JudgeMode.DEFAULT.getMode().equals(judgeMode)) {
            // 原数据MD5
            caseObject.set("outputMd5", DigestUtils.md5DigestAsHex(outputData.getBytes(StandardCharsets.UTF_8)));
            // 原数据大小
            caseObject.set("outputSize", outputData.getBytes(StandardCharsets.UTF_8).length);
            // 去掉全部空格的MD5，用来判断pe
            caseObject.set("allStrippedOutputMd5", DigestUtils.md5DigestAsHex(outputData.replaceAll("\\s+", "").getBytes(StandardCharsets.UTF_8)));
            // 默认去掉文末空格的MD5
            caseObject.set("EOFStrippedOutputMd5", DigestUtils.md5DigestAsHex(FileUtils.rtrim(outputData).getBytes(StandardCharsets.UTF_8)));
        }
    }

    private String getInfoFilePath(String testCasesDir) {
        return testCasesDir + File.separator + "info";
    }

}
