package love.pangteen.problem.utils;

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
import love.pangteen.problem.pojo.entity.ProblemCase;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/22 19:04
 **/
@Component
public class FileUtils {

    private final static Pattern EOL_PATTERN = Pattern.compile("[^\\S\\n]+(?=\\n)");

    /**
     * 初始化上传文件的测试数据，写成json文件。
     */
    @Async
    public void initUploadTestCase(String judgeMode, String judgeCaseMode, String version, Long problemId, String tmpTestcaseDir, List<ProblemCase> problemCaseList) {
        String testCasesDir = OJFiles.getTestCaseFolder(problemId);

        // 将之前的临时文件夹里面的评测文件全部复制到指定文件夹(覆盖)。
        if (!StrUtil.isEmpty(tmpTestcaseDir) && FileUtil.exist(tmpTestcaseDir) && !FileUtil.isDirEmpty(new File(tmpTestcaseDir))) {
            FileUtil.clean(testCasesDir);
            File testCasesDirFile = new File(testCasesDir);
            FileUtil.copyFilesFromDir(new File(tmpTestcaseDir), testCasesDirFile, true);
        }

        List<String> listFileNames = FileUtil.listFileNames(testCasesDir);

        JSONObject result = new JSONObject();
        result.set("mode", judgeMode);
        result.set("judgeCaseMode", judgeCaseMode);
        result.set("version", version);
        result.set("testCasesSize", problemCaseList.size());

        JSONArray testCaseList = new JSONArray(problemCaseList.size());

        for (ProblemCase problemCase : problemCaseList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.set("caseId", problemCase.getId());
            if (judgeCaseMode.equals(JudgeCaseMode.SUBTASK_AVERAGE.getMode()) || judgeCaseMode.equals(JudgeCaseMode.SUBTASK_LOWEST.getMode())) {
                jsonObject.set("groupNum", problemCase.getGroupNum());
            }

            jsonObject.set("score", problemCase.getScore());
            jsonObject.set("inputName", problemCase.getInput());
            jsonObject.set("outputName", problemCase.getOutput());

            listFileNames.remove(problemCase.getInput());
            listFileNames.remove(problemCase.getOutput());

            // 读取输入文件
            FileReader inputFile = new FileReader(testCasesDir + File.separator + problemCase.getInput(), CharsetUtil.UTF_8);
            String input = inputFile.readString()
                    .replaceAll("\r\n", "\n") // 避免window系统的换行问题
                    .replaceAll("\r", "\n"); // 避免mac系统的换行问题

            FileWriter inputFileWriter = new FileWriter(testCasesDir + File.separator + problemCase.getInput(), CharsetUtil.UTF_8);
            inputFileWriter.write(input);

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

            // spj和interactive是根据特判程序输出判断结果，所以无需初始化测试数据
            if (JudgeMode.DEFAULT.getMode().equals(judgeMode)) {
                // 原数据MD5
                jsonObject.set("outputMd5", DigestUtils.md5DigestAsHex(output.getBytes(StandardCharsets.UTF_8)));
                // 原数据大小
                jsonObject.set("outputSize", output.getBytes(StandardCharsets.UTF_8).length);
                // 去掉全部空格的MD5，用来判断pe
                jsonObject.set("allStrippedOutputMd5", DigestUtils.md5DigestAsHex(output.replaceAll("\\s+", "").getBytes(StandardCharsets.UTF_8)));
                // 默认去掉文末空格的MD5
                jsonObject.set("EOFStrippedOutputMd5", DigestUtils.md5DigestAsHex(rtrim(output).getBytes(StandardCharsets.UTF_8)));
            }

            testCaseList.add(jsonObject);
        }

        result.set("testCases", testCaseList);

        FileWriter infoFile = new FileWriter(testCasesDir + "/info", CharsetUtil.UTF_8);
        // 写入记录文件
        infoFile.write(JSONUtil.toJsonStr(result));
        // 删除临时上传文件夹
        FileUtil.del(tmpTestcaseDir);
        // 删除非测试数据的文件
        listFileNames.remove("info");
        if (!CollUtil.isEmpty(listFileNames)) {
            for (String filename : listFileNames) {
                FileUtil.del(testCasesDir + File.separator + filename);
            }
        }
    }

    /**
     * 初始化手动输入上传的测试数据，写成json文件。
     */
    @Async
    public void initHandTestCase(String judgeMode, String judgeCaseMode, String version, Long problemId, List<ProblemCase> problemCaseList) {
        JSONObject result = new JSONObject();
        result.set("mode", judgeMode);
        if (StrUtil.isEmpty(judgeCaseMode)) {
            judgeCaseMode = JudgeCaseMode.DEFAULT.getMode();
        }
        result.set("judgeCaseMode", judgeCaseMode);
        result.set("version", version);
        result.set("testCasesSize", problemCaseList.size());

        JSONArray testCaseList = new JSONArray(problemCaseList.size());

        String testCasesDir = OJFiles.getTestCaseFolder(problemId);
        FileUtil.del(testCasesDir);
        for (int index = 0; index < problemCaseList.size(); index++) {
            JSONObject jsonObject = new JSONObject();
            String inputName = (index + 1) + ".in";
            jsonObject.set("caseId", problemCaseList.get(index).getId());
            if (judgeCaseMode.equals(JudgeCaseMode.SUBTASK_AVERAGE.getMode()) || judgeCaseMode.equals(JudgeCaseMode.SUBTASK_LOWEST.getMode())) {
                jsonObject.set("groupNum", problemCaseList.get(index).getGroupNum());
            }
            jsonObject.set("score", problemCaseList.get(index).getScore());
            jsonObject.set("inputName", inputName);
            // 生成对应文件
            FileWriter infileWriter = new FileWriter(testCasesDir + "/" + inputName, CharsetUtil.UTF_8);
            // 将该测试数据的输入写入到文件
            String inputData = problemCaseList
                    .get(index)
                    .getInput()
                    .replaceAll("\r\n", "\n") // 避免window系统的换行问题
                    .replaceAll("\r", "\n"); // 避免mac系统的换行问题
            infileWriter.write(inputData);

            String outputName = (index + 1) + ".out";
            jsonObject.set("outputName", outputName);
            // 生成对应文件
            String outputData = problemCaseList
                    .get(index)
                    .getOutput()
                    .replaceAll("\r\n", "\n") // 避免window系统的换行问题
                    .replaceAll("\r", "\n"); // 避免mac系统的换行问题
            FileWriter outFile = new FileWriter(testCasesDir + "/" + outputName, CharsetUtil.UTF_8);
            outFile.write(outputData);

            // spj和interactive是根据特判程序输出判断结果，所以无需初始化测试数据
            if (JudgeMode.DEFAULT.getMode().equals(judgeMode)) {
                // 原数据MD5
                jsonObject.set("outputMd5", DigestUtils.md5DigestAsHex(outputData.getBytes(StandardCharsets.UTF_8)));
                // 原数据大小
                jsonObject.set("outputSize", outputData.getBytes(StandardCharsets.UTF_8).length);
                // 去掉全部空格的MD5，用来判断pe
                jsonObject.set("allStrippedOutputMd5", DigestUtils.md5DigestAsHex(outputData.replaceAll("\\s+", "").getBytes(StandardCharsets.UTF_8)));
                // 默认去掉文末空格的MD5
                jsonObject.set("EOFStrippedOutputMd5", DigestUtils.md5DigestAsHex(rtrim(outputData).getBytes(StandardCharsets.UTF_8)));
            }

            testCaseList.add(jsonObject);
        }

        result.set("testCases", testCaseList);

        FileWriter infoFile = new FileWriter(testCasesDir + "/info", CharsetUtil.UTF_8);
        // 写入记录文件
        infoFile.write(JSONUtil.toJsonStr(result));
    }

    /**
     * 去除每行末尾的空白符。
     */
    public static String rtrim(String value) {
        if (value == null) return null;
        return EOL_PATTERN.matcher(StrUtil.trimEnd(value)).replaceAll("");
    }

}
