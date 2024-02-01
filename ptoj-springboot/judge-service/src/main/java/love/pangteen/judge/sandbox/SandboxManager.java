package love.pangteen.judge.sandbox;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import love.pangteen.api.enums.JudgeStatus;
import love.pangteen.exception.JudgeSystemError;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/25 15:22
 **/
@Slf4j
public class SandboxManager {

    public static final HashMap<String, JudgeStatus> RESULT_MAP_STATUS = new HashMap<>();
    private static final List<String> SIGNALS = new ArrayList<>();
    private static final SandboxManager INSTANCE = new SandboxManager();
    private static final JSONArray COMPILE_FILES = new JSONArray();
    private static final String SANDBOX_BASE_URL = "http://localhost:5050";
    private static final RestTemplate REST_TEMPLATE;
    private static final int MAX_PROCESS_NUM = 128;

    private static final int TIME_LIMIT_MS = 16000;

    private static final int MEMORY_LIMIT_MB = 512;

    private static final int STACK_LIMIT_MB = 128;

    private static final int STDIO_SIZE_MB = 32;

    static {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(20000);
        requestFactory.setReadTimeout(180000);
        REST_TEMPLATE = new RestTemplate(requestFactory);
    }

    static {
        RESULT_MAP_STATUS.putAll(Map.of(
                Status.ACCEPTED, JudgeStatus.STATUS_ACCEPTED,
                Status.MEMORY_LIMIT_EXCEEDED, JudgeStatus.STATUS_MEMORY_LIMIT_EXCEEDED,
                Status.TIME_LIMIT_EXCEEDED, JudgeStatus.STATUS_TIME_LIMIT_EXCEEDED,
                Status.OUTPUT_LIMIT_EXCEEDED, JudgeStatus.STATUS_RUNTIME_ERROR,
                Status.FILE_ERROR, JudgeStatus.STATUS_SYSTEM_ERROR,
                Status.NONZERO_EXIT_STATUS, JudgeStatus.STATUS_RUNTIME_ERROR,
                Status.SIGNALLED, JudgeStatus.STATUS_RUNTIME_ERROR,
                Status.INTERNAL_ERROR, JudgeStatus.STATUS_SYSTEM_ERROR
        ));
    }

    static {
        SIGNALS.addAll(List.of(
                "", // 0
                "Hangup", // 1
                "Interrupt", // 2
                "Quit", // 3
                "Illegal instruction", // 4
                "Trace/breakpoint trap", // 5
                "Aborted", // 6
                "Bus error", // 7
                "Floating point exception", // 8
                "Killed", // 9
                "User defined signal 1", // 10
                "Segmentation fault", // 11
                "User defined signal 2", // 12
                "Broken pipe", // 13
                "Alarm clock", // 14
                "Terminated", // 15
                "Stack fault", // 16
                "Child exited", // 17
                "Continued", // 18
                "Stopped (signal)", // 19
                "Stopped", // 20
                "Stopped (tty input)", // 21
                "Stopped (tty output)", // 22
                "Urgent I/O condition", // 23
                "CPU time limit exceeded", // 24
                "File size limit exceeded", // 25
                "Virtual timer expired", // 26
                "Profiling timer expired", // 27
                "Window changed", // 28
                "I/O possible", // 29
                "Power failure", // 30
                "Bad system call" // 31
        ));
    }

    /*
     * "files": [{
     * "content": ""
     * }, {
     * "name": "stdout",
     * "max": 1024 * 1024 * 32
     * }, {
     * "name": "stderr",
     * "max": 1024 * 1024 * 32
     * }]
     */
    static {
        JSONObject content = new JSONObject();
        content.set("content", "");

        JSONObject stdout = new JSONObject();
        stdout.set("name", "stdout");
        stdout.set("max", 1024 * 1024 * STDIO_SIZE_MB);

        JSONObject stderr = new JSONObject();
        stderr.set("name", "stderr");
        stderr.set("max", 1024 * 1024 * STDIO_SIZE_MB);
        COMPILE_FILES.put(content);
        COMPILE_FILES.put(stdout);
        COMPILE_FILES.put(stderr);
    }

    public static RestTemplate getRestTemplate() {
        return REST_TEMPLATE;
    }

    public static String getSandboxBaseUrl() {
        return SANDBOX_BASE_URL;
    }

    public static SandboxManager get(){
        return INSTANCE;
    }

    public static String getSandboxUrl(String uri) {
        return getSandboxBaseUrl() + uri;
    }

    public static Integer getStatusCode(String status){
        return RESULT_MAP_STATUS.get(status).getStatus();
    }

    public static String getSignal(int signal) {
        return SIGNALS.get(signal);
    }

    /**
     * 通过RestTemplate访问沙箱来运行代码。
     */
    public JSONArray run(JSONObject param) throws JudgeSystemError {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(JSONUtil.toJsonStr(param), headers);
        try {
            ResponseEntity<String> response = getRestTemplate().postForEntity(getSandboxUrl("/run"), request, String.class);
            return JSONUtil.parseArray(response.getBody());
        } catch (RestClientResponseException ex) {
            if (ex.getRawStatusCode() != 200) {
                throw new JudgeSystemError("Cannot connect to sandbox service.", null, ex.getResponseBodyAsString());
            }
        } catch (Exception e) {
            throw new JudgeSystemError("Call SandBox Error.", null, e.getMessage());
        }
        return null;
    }

    public static String version(){
        return getRestTemplate().getForObject(getSandboxUrl("/version"), String.class);
    }

    /**
     * 需要注意，在生产环境中 copyOutCached 产生的文件在使用完之后需要使用（DELETE /file/:id）删除避免内存泄露。
     */
    public static void delFile(String fileId) {
        try {
            getRestTemplate().delete(getSandboxUrl("/file/{0}"), fileId);
        } catch (RestClientResponseException ex) {
            if (ex.getRawStatusCode() != 200) {
                log.error("安全沙箱判题的删除内存中的文件缓存操作异常----------------->{}", ex.getResponseBodyAsString());
            }
        }
    }

    /**
     * 编译运行。
     * @param maxCpuTime        最大编译的cpu时间 ms
     * @param maxRealTime       最大编译的真实时间 ms
     * @param maxMemory         最大编译的空间 b
     * @param maxStack          最大编译的栈空间 b
     * @param srcName           编译的源文件名字
     * @param exeName           编译生成的exe文件名字
     * @param args              编译的cmd参数
     * @param envs              编译的环境变量
     * @param code              编译的源代码
     * @param extraFiles        编译所需的额外文件 key:文件名，value:文件内容
     * @param needCopyOutCached 是否需要生成用户程序的缓存文件，即生成用户程序id
     * @param needCopyOutExe    是否需要生成编译后的用户程序exe文件
     * @param copyOutDir        生成编译后的用户程序exe文件的指定路径
     * @Return
     */
    public static JSONArray compile(Long maxCpuTime,
                                    Long maxRealTime,
                                    Long maxMemory,
                                    Long maxStack,
                                    String srcName,
                                    String exeName,
                                    List<String> args,
                                    List<String> envs,
                                    String code,
                                    HashMap<String, String> extraFiles,
                                    Boolean needCopyOutCached,
                                    Boolean needCopyOutExe,
                                    String copyOutDir) throws JudgeSystemError {
        JSONObject cmd = new JSONObject();
        cmd.set("args", args);
        cmd.set("env", envs);
        cmd.set("files", COMPILE_FILES);
        // ms-->ns
        cmd.set("cpuLimit", maxCpuTime * 1000 * 1000L);
        cmd.set("clockLimit", maxRealTime * 1000 * 1000L);
        // byte
        cmd.set("memoryLimit", maxMemory);
        cmd.set("procLimit", MAX_PROCESS_NUM);
        cmd.set("stackLimit", maxStack);

        JSONObject fileContent = new JSONObject();
        fileContent.set("content", code);

        JSONObject copyIn = new JSONObject();
        copyIn.set(srcName, fileContent);

        if (extraFiles != null) {
            for (Map.Entry<String, String> entry : extraFiles.entrySet()) {
                if (!StrUtil.isEmpty(entry.getKey()) && !StrUtil.isEmpty(entry.getValue())) {
                    JSONObject content = new JSONObject();
                    content.set("content", entry.getValue());
                    copyIn.set(entry.getKey(), content);
                }
            }
        }

        cmd.set("copyIn", copyIn);
        cmd.set("copyOut", new JSONArray().put("stdout").put("stderr"));

        if (needCopyOutCached) {
            cmd.set("copyOutCached", new JSONArray().put(exeName));
        }

        if (needCopyOutExe) {
            cmd.set("copyOutDir", copyOutDir);
        }

        JSONObject param = new JSONObject();
        param.set("cmd", new JSONArray().put(cmd));

        JSONArray result = get().run(param);

        JSONObject compileRes = (JSONObject) result.get(0);
        String status = compileRes.getStr("status");
        compileRes.set("originalStatus", status);
        compileRes.set("status", getStatusCode(status));
        return result;
    }


    /**
     * @param args            普通评测运行cmd的命令参数
     * @param envs            普通评测运行的环境变量
     * @param testCasePath    题目数据的输入文件路径
     * @param testCaseContent 题目数据的输入数据（与testCasePath二者选一）
     * @param maxTime         评测的最大限制时间 ms
     * @param maxOutputSize   评测的最大输出大小 kb
     * @param maxStack        评测的最大限制栈空间 mb
     * @param exeName         评测的用户程序名称
     * @param fileId          评测的用户程序文件id
     * @param fileContent     评测的用户程序文件内容，如果userFileId存在则为null
     * @param isFileIO        是否为文件IO
     * @param ioReadFileName  题目指定的io输入文件的名称
     * @param ioWriteFileName 题目指定的io输出文件的名称
     * @MethodName testCase
     * @Description 普通评测
     * @Return JSONArray
     * @Since 2022/1/3
     */
    public static JSONArray testCase(List<String> args,
                                     List<String> envs,
                                     String testCasePath,
                                     String testCaseContent,
                                     Long maxTime,
                                     Long maxMemory,
                                     Long maxOutputSize,
                                     Integer maxStack,
                                     String exeName,
                                     String fileId,
                                     String fileContent,
                                     Boolean isFileIO,
                                     String ioReadFileName,
                                     String ioWriteFileName) throws JudgeSystemError {

        JSONObject cmd = new JSONObject();
        cmd.set("args", args);
        cmd.set("env", envs);

        JSONArray files = new JSONArray();

        JSONObject testCaseInput = new JSONObject();
        if (StrUtil.isEmpty(testCasePath)) {
            testCaseInput.set("content", testCaseContent);
        } else {
            testCaseInput.set("src", testCasePath);
        }

        if (BooleanUtil.isFalse(isFileIO)) {
            JSONObject stdout = new JSONObject();
            stdout.set("name", "stdout");
            stdout.set("max", maxOutputSize);
            files.put(testCaseInput);
            files.put(stdout);
        }

        JSONObject stderr = new JSONObject();
        stderr.set("name", "stderr");
        stderr.set("max", 1024 * 1024 * 16);
        files.put(stderr);

        cmd.set("files", files);

        // ms-->ns
        cmd.set("cpuLimit", maxTime * 1000 * 1000L);
        cmd.set("clockLimit", maxTime * 1000 * 1000L * 3);
        // byte
        cmd.set("memoryLimit", (maxMemory + 100) * 1024 * 1024L);
        cmd.set("procLimit", MAX_PROCESS_NUM);
        cmd.set("stackLimit", maxStack * 1024 * 1024L);

        JSONObject exeFile = new JSONObject();
        if (!StrUtil.isEmpty(fileId)) {
            exeFile.set("fileId", fileId);
        } else {
            exeFile.set("content", fileContent);
        }
        JSONObject copyIn = new JSONObject();
        copyIn.set(exeName, exeFile);

        JSONArray copyOut = new JSONArray();
        copyOut.put("stderr");
        if (BooleanUtil.isFalse(isFileIO)){
            copyOut.put("stdout");
        }else{
            copyIn.set(ioReadFileName, testCaseInput);
            // 在文件名之后加入 '?' 来使文件变为可选，可选文件不存在的情况不会触发 FileError
            copyOut.put(ioWriteFileName + "?");
        }

        cmd.set("copyIn", copyIn);
        cmd.set("copyOut", copyOut);

        JSONObject param = new JSONObject();
        param.set("cmd", new JSONArray().put(cmd));

        // 调用判题安全沙箱
        JSONArray result = get().run(param);

        JSONObject testcaseRes = (JSONObject) result.get(0);
        testcaseRes.set("originalStatus", testcaseRes.getStr("status"));
        testcaseRes.set("status", RESULT_MAP_STATUS.get(testcaseRes.getStr("status")));
        return result;
    }


    /**
     * @param args                   特殊判题的运行cmd命令参数
     * @param envs                   特殊判题的运行环境变量
     * @param userOutputFilePath     用户程序输出文件的路径
     * @param userOutputFileName     用户程序输出文件的名字
     * @param testCaseInputFilePath  题目数据的输入文件的路径
     * @param testCaseInputFileName  题目数据的输入文件的名字
     * @param testCaseOutputFilePath 题目数据的输出文件的路径
     * @param testCaseOutputFileName 题目数据的输出文件的路径
     * @param spjExeSrc              特殊判题的exe文件的路径
     * @param spjExeName             特殊判题的exe文件的名字
     * @MethodName spjCheckResult
     * @Description 特殊判题的评测
     * @Return JSONArray
     * @Since 2022/1/3
     */
    public static JSONArray spjCheckResult(List<String> args,
                                           List<String> envs,
                                           String userOutputFilePath,
                                           String userOutputFileName,
                                           String testCaseInputFilePath,
                                           String testCaseInputFileName,
                                           String testCaseOutputFilePath,
                                           String testCaseOutputFileName,
                                           String spjExeSrc,
                                           String spjExeName) throws JudgeSystemError {

        JSONObject cmd = new JSONObject();
        cmd.set("args", args);
        cmd.set("env", envs);

        JSONArray outFiles = new JSONArray();

        JSONObject content = new JSONObject();
        content.set("content", "");

        JSONObject outStdout = new JSONObject();
        outStdout.set("name", "stdout");
        outStdout.set("max", 1024 * 1024 * 16);

        JSONObject outStderr = new JSONObject();
        outStderr.set("name", "stderr");
        outStderr.set("max", 1024 * 1024 * 16);

        outFiles.put(content);
        outFiles.put(outStdout);
        outFiles.put(outStderr);
        cmd.set("files", outFiles);

        // ms-->ns
        cmd.set("cpuLimit", TIME_LIMIT_MS * 1000 * 1000L);
        cmd.set("clockLimit", TIME_LIMIT_MS * 1000 * 1000L * 3);
        // byte
        cmd.set("memoryLimit", MEMORY_LIMIT_MB * 1024 * 1024L);
        cmd.set("procLimit", MAX_PROCESS_NUM);
        cmd.set("stackLimit", STACK_LIMIT_MB * 1024 * 1024L);


        JSONObject spjExeFile = new JSONObject();
        spjExeFile.set("src", spjExeSrc);

        JSONObject useOutputFileSrc = new JSONObject();
        useOutputFileSrc.set("src", userOutputFilePath);

        JSONObject stdInputFileSrc = new JSONObject();
        stdInputFileSrc.set("src", testCaseInputFilePath);

        JSONObject stdOutFileSrc = new JSONObject();
        stdOutFileSrc.set("src", testCaseOutputFilePath);

        JSONObject spjCopyIn = new JSONObject();

        spjCopyIn.set(spjExeName, spjExeFile);
        spjCopyIn.set(userOutputFileName, useOutputFileSrc);
        spjCopyIn.set(testCaseInputFileName, stdInputFileSrc);
        spjCopyIn.set(testCaseOutputFileName, stdOutFileSrc);


        cmd.set("copyIn", spjCopyIn);
        cmd.set("copyOut", new JSONArray().put("stdout").put("stderr"));

        JSONObject param = new JSONObject();

        param.set("cmd", new JSONArray().put(cmd));

        // 调用判题安全沙箱
        JSONArray result =get().run(param);
        JSONObject spjRes = (JSONObject) result.get(0);
        spjRes.set("originalStatus", spjRes.getStr("status"));
        spjRes.set("status", RESULT_MAP_STATUS.get(spjRes.getStr("status")));
        return result;
    }


    /**
     * @param args                   cmd的命令参数 评测运行的命令
     * @param envs                   测评的环境变量
     * @param userExeName            用户程序的名字
     * @param userFileId             用户程序在编译后返回的id，主要是对应内存中已编译后的文件
     * @param userFileContent        用户程序文件的内容，如果userFileId存在则为null
     * @param userMaxTime            用户程序的最大测评时间 ms
     * @param userMaxStack           用户程序的最大测评栈空间 mb
     * @param testCaseInputPath      题目数据的输入文件路径
     * @param testCaseInputFileName  题目数据的输入文件名字
     * @param testCaseOutputFilePath 题目数据的输出文件路径
     * @param testCaseOutputFileName 题目数据的输出文件名字
     * @param userOutputFileName     用户程序的输出文件名字
     * @param interactArgs           交互程序运行的cmd命令参数
     * @param interactEnvs           交互程序运行的环境变量
     * @param interactExeSrc         交互程序的exe文件路径
     * @param interactExeName        交互程序的exe文件名字
     * @MethodName interactTestCase
     * @Description 交互评测
     * @Return JSONArray
     * @Since 2022/1/3
     */
    public static JSONArray interactTestCase(List<String> args,
                                             List<String> envs,
                                             String userExeName,
                                             String userFileId,
                                             String userFileContent,
                                             Long userMaxTime,
                                             Long userMaxMemory,
                                             Integer userMaxStack,
                                             String testCaseInputPath,
                                             String testCaseInputFileName,
                                             String testCaseOutputFilePath,
                                             String testCaseOutputFileName,
                                             String userOutputFileName,
                                             List<String> interactArgs,
                                             List<String> interactEnvs,
                                             String interactExeSrc,
                                             String interactExeName) throws JudgeSystemError {

        /**
         *  注意：用户源代码需要先编译，若是通过编译需要先将文件存入内存，再利用管道判题，同时特殊判题程序必须已编译且存在（否则判题失败，系统错误）！
         */

        JSONObject pipeInputCmd = new JSONObject();
        pipeInputCmd.set("args", args);
        pipeInputCmd.set("env", envs);

        JSONArray files = new JSONArray();

        JSONObject stderr = new JSONObject();
        stderr.set("name", "stderr");
        stderr.set("max", 1024 * 1024 * STDIO_SIZE_MB);

        files.put(new JSONObject());
        files.put(new JSONObject());
        files.put(stderr);

        String inTmp = files.toString().replace("{}", "null");
        pipeInputCmd.set("files", JSONUtil.parseArray(inTmp, false));

        // ms-->ns
        pipeInputCmd.set("cpuLimit", userMaxTime * 1000 * 1000L);
        pipeInputCmd.set("clockLimit", userMaxTime * 1000 * 1000L * 3);

        // byte

        pipeInputCmd.set("memoryLimit", (userMaxMemory + 100) * 1024 * 1024L);
        pipeInputCmd.set("procLimit", MAX_PROCESS_NUM);
        pipeInputCmd.set("stackLimit", userMaxStack * 1024 * 1024L);

        JSONObject exeFile = new JSONObject();
        if (!StrUtil.isEmpty(userFileId)) {
            exeFile.set("fileId", userFileId);
        } else {
            exeFile.set("content", userFileContent);
        }
        JSONObject copyIn = new JSONObject();
        copyIn.set(userExeName, exeFile);

        pipeInputCmd.set("copyIn", copyIn);
        pipeInputCmd.set("copyOut", new JSONArray());


        // 管道输出，用户程序输出数据经过特殊判题程序后，得到的最终输出结果。
        JSONObject pipeOutputCmd = new JSONObject();
        pipeOutputCmd.set("args", interactArgs);
        pipeOutputCmd.set("env", interactEnvs);

        JSONArray outFiles = new JSONArray();


        JSONObject outStderr = new JSONObject();
        outStderr.set("name", "stderr");
        outStderr.set("max", 1024 * 1024 * STDIO_SIZE_MB);
        outFiles.put(new JSONObject());
        outFiles.put(new JSONObject());
        outFiles.put(outStderr);
        String outTmp = outFiles.toString().replace("{}", "null");
        pipeOutputCmd.set("files", JSONUtil.parseArray(outTmp, false));

        // ms-->ns
        pipeOutputCmd.set("cpuLimit", userMaxTime * 1000 * 1000L * 2);
        pipeOutputCmd.set("clockLimit", userMaxTime * 1000 * 1000L * 3 * 2);
        // byte
        pipeOutputCmd.set("memoryLimit", (userMaxMemory + 100) * 1024 * 1024L * 2);
        pipeOutputCmd.set("procLimit", MAX_PROCESS_NUM);
        pipeOutputCmd.set("stackLimit", STACK_LIMIT_MB * 1024 * 1024L);

        JSONObject spjExeFile = new JSONObject();
        spjExeFile.set("src", interactExeSrc);

        JSONObject stdInputFileSrc = new JSONObject();
        stdInputFileSrc.set("src", testCaseInputPath);

        JSONObject stdOutFileSrc = new JSONObject();
        stdOutFileSrc.set("src", testCaseOutputFilePath);

        JSONObject interactiveCopyIn = new JSONObject();
        interactiveCopyIn.set(interactExeName, spjExeFile);
        interactiveCopyIn.set(testCaseInputFileName, stdInputFileSrc);
        interactiveCopyIn.set(testCaseOutputFileName, stdOutFileSrc);


        pipeOutputCmd.set("copyIn", interactiveCopyIn);
        pipeOutputCmd.set("copyOut", new JSONArray().put(userOutputFileName));

        JSONArray cmdList = new JSONArray();
        cmdList.put(pipeInputCmd);
        cmdList.put(pipeOutputCmd);

        JSONObject param = new JSONObject();
        // 添加cmd指令
        param.set("cmd", cmdList);

        // 添加管道映射
        JSONArray pipeMapping = new JSONArray();
        // 用户程序
        JSONObject user = new JSONObject();

        JSONObject userIn = new JSONObject();
        userIn.set("index", 0);
        userIn.set("fd", 1);

        JSONObject userOut = new JSONObject();
        userOut.set("index", 1);
        userOut.set("fd", 0);

        user.set("in", userIn);
        user.set("out", userOut);
        user.set("max", STDIO_SIZE_MB * 1024 * 1024);
        user.set("proxy", true);
        user.set("name", "stdout");

        // 评测程序
        JSONObject judge = new JSONObject();

        JSONObject judgeIn = new JSONObject();
        judgeIn.set("index", 1);
        judgeIn.set("fd", 1);

        JSONObject judgeOut = new JSONObject();
        judgeOut.set("index", 0);
        judgeOut.set("fd", 0);

        judge.set("in", judgeIn);
        judge.set("out", judgeOut);
        judge.set("max", STDIO_SIZE_MB * 1024 * 1024);
        judge.set("proxy", true);
        judge.set("name", "stdout");


        // 添加到管道映射列表
        pipeMapping.add(user);
        pipeMapping.add(judge);

        param.set("pipeMapping", pipeMapping);

        // 调用判题安全沙箱
        JSONArray result = get().run(param);
        JSONObject userRes = (JSONObject) result.get(0);
        JSONObject interactiveRes = (JSONObject) result.get(1);
        userRes.set("originalStatus", userRes.getStr("status"));
        userRes.set("status", RESULT_MAP_STATUS.get(userRes.getStr("status")));
        interactiveRes.set("originalStatus", interactiveRes.getStr("status"));
        interactiveRes.set("status", RESULT_MAP_STATUS.get(interactiveRes.getStr("status")));
        return result;
    }

    private interface Status {
        String ACCEPTED = "Accepted"; // 正常情况
        String MEMORY_LIMIT_EXCEEDED = "Memory Limit Exceeded"; // 内存超限
        String TIME_LIMIT_EXCEEDED = "Time Limit Exceeded"; // 时间超限
        String OUTPUT_LIMIT_EXCEEDED = "Output Limit Exceeded"; // 输出超限
        String FILE_ERROR = "File Error"; // 文件错误
        String NONZERO_EXIT_STATUS = "Nonzero Exit Status"; // 非 0 退出值
        String SIGNALLED = "Signalled"; // 进程被信号终止
        String INTERNAL_ERROR = "Internal Error"; // 内部错误
    }

}
 /*
     1. compile
        Json Request Body
        {
            "cmd": [{
                "args": ["/usr/bin/g++", "a.c", "-o", "a"],
                "env": ["PATH=/usr/bin:/bin"],
                "files": [{
                    "content": ""
                }, {
                    "name": "stdout",
                    "max": 10240
                }, {
                    "name": "stderr",
                    "max": 10240
                }],
                "cpuLimit": 10000000000,
                "memoryLimit": 104857600,
                "procLimit": 50,
                "copyIn": {
                    "a.c": {
                        "content": "#include <iostream>\nusing namespace std;\nint main() {\nint a, b;\ncin >> a >> b;\ncout << a + b << endl;\n}"
                    }
                },
                "copyOut": ["stdout", "stderr"],
                "copyOutCached": ["a.cc", "a"],
                "copyOutDir": "1"
            }]
        }

        Json Response Data

        [
            {
                "status": "Accepted",
                "exitStatus": 0,
                "time": 303225231,
                "memory": 32243712,
                "runTime": 524177700,
                "files": {
                    "stderr": "",
                    "stdout": ""
                },
                "fileIds": {
                    "a": "5LWIZAA45JHX4Y4Z",
                    "a.cc": "NOHPGGDTYQUFRSLJ"
                }
            }
        ]
    2.test case

            Json Request Body
              {
                "cmd": [{
                    "args": ["a"],
                    "env": ["PATH=/usr/bin:/bin","LANG=en_US.UTF-8","LC_ALL=en_US.UTF-8","LANGUAGE=en_US:en"],
                    "files": [{
                        "src": "/judge/test_case/problem_1010/1.in"
                    }, {
                        "name": "stdout",
                        "max": 10240
                    }, {
                        "name": "stderr",
                        "max": 10240
                    }],
                    "cpuLimit": 10000000000,
                    "realCpuLimit":30000000000,
                    "stackLimit":134217728,
                    "memoryLimit": 104811111,
                    "procLimit": 50,
                    "copyIn": {
                        "a":{"fileId":"WDQL5TNLRRVB2KAP"}
                    },
                    "copyOut": ["stdout", "stderr"]
                }]
            }

            Json Response Data
             [{
              "status": "Accepted",
              "exitStatus": 0,
              "time": 3171607,
              "memory": 475136,
              "runTime": 110396333,
              "files": {
                "stderr": "",
                "stdout": "23\n"
              }
            }]

    3. Interactive

        {
    "pipeMapping": [
        {
            "in": {
                "max": 16777216,
                "index": 0,
                "fd": 1
            },
            "out": {
                "index": 1,
                "fd": 0
            }
        }
    ],
    "cmd": [
        {
            "stackLimit": 134217728,
            "cpuLimit": 3000000000,
            "realCpuLimit": 9000000000,
            "clockLimit": 64,
            "env": [
                "LANG=en_US.UTF-8",
                "LANGUAGE=en_US:en",
                "LC_ALL=en_US.UTF-8",
                "PYTHONIOENCODING=utf-8"
            ],
            "copyOut": [
                "stderr"
            ],
            "args": [
                "/usr/bin/python3",
                "main"
            ],
            "files": [
                {
                    "src": "/judge/test_case/problem_1002/5.in"
                },
                null,
                {
                    "max": 16777216,
                    "name": "stderr"
                }
            ],
            "memoryLimit": 536870912,
            "copyIn": {
                "main": {
                    "fileId": "CGTRDEMKW5VAYN6O"
                }
            }
        },
        {
            "stackLimit": 134217728,
            "cpuLimit": 8000000000,
            "clockLimit": 24000000000,
            "env": [
                "PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin",
                "LANG=en_US.UTF-8",
                "LANGUAGE=en_US:en",
                "LC_ALL=en_US.UTF-8"
            ],
            "copyOut": [
                "stdout",
                "stderr"
            ],
            "args": [
                "/w/spj",
                "/w/tmp"
            ],
            "files": [
                null,
                {
                    "max": 16777216,
                    "name": "stdout"
                },
                {
                    "max": 16777216,
                    "name": "stderr"
                }
            ],
            "memoryLimit": 536870912,
            "copyIn": {
                "spj": {
                    "src": "/judge/spj/1002/spj"
                },
                "tmp": {
                    "src": "/judge/test_case/problem_1002/5.out"
                }
            },
            "procLimit": 64
        }
    ]
}
}
*/