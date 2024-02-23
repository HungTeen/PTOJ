package love.pangteen.judge.sandbox;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import love.pangteen.api.constant.OJFiles;
import love.pangteen.api.enums.JudgeMode;
import love.pangteen.api.pojo.dto.TestJudgeDTO;
import love.pangteen.api.pojo.entity.Problem;
import love.pangteen.api.utils.JudgeUtils;
import love.pangteen.exception.JudgeSystemError;
import love.pangteen.judge.manager.LanguageManager;
import love.pangteen.judge.pojo.dto.JudgeDTO;
import love.pangteen.judge.pojo.dto.JudgeGlobalDTO;
import love.pangteen.judge.pojo.entity.LanguageConfig;
import love.pangteen.api.pojo.entity.TestJudgeResult;
import love.pangteen.judge.sandbox.judge.AbstractJudge;
import love.pangteen.judge.sandbox.judge.DefaultJudge;
import love.pangteen.judge.sandbox.judge.TestJudge;
import love.pangteen.judge.utils.ThreadPoolUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/2/1 10:25
 **/
@Component
public class JudgeRunner {

    @Resource
    private TestJudge testJudge;

    @Resource
    private DefaultJudge defaultJudge;

    /**
     * 运行自测评测单个测试点（由接口传入 输入与输出的数据）。
     */
    public TestJudgeResult testJudgeCase(String userFileId, TestJudgeDTO testJudgeDTO) throws ExecutionException, InterruptedException {
        // 默认给限制时间+200ms用来测评
        Long testTime = testJudgeDTO.getTimeLimit() + 200L;

        LanguageConfig runConfig = LanguageManager.getLanguageConfigByName(testJudgeDTO.getLanguage());

        JudgeGlobalDTO judgeGlobalDTO = JudgeGlobalDTO.builder()
                .judgeMode(JudgeMode.TEST)
                .userFileId(userFileId)
                .userFileContent(testJudgeDTO.getCode())
                .testTime(testTime)
                .maxMemory((long) testJudgeDTO.getMemoryLimit())
                .maxTime((long) testJudgeDTO.getTimeLimit())
                .maxStack(testJudgeDTO.getStackLimit())
                .removeEOLBlank(testJudgeDTO.getIsRemoveEndBlank())
                .isFileIO(testJudgeDTO.getIsFileIO())
                .ioReadFileName(testJudgeDTO.getIoReadFileName())
                .ioWriteFileName(testJudgeDTO.getIoWriteFileName())
                .runConfig(runConfig)
                .build();

        Long maxOutputSize = Math.max(testJudgeDTO.getTestCaseInput().length() * 2L, 32 * 1024 * 1024L);

        JudgeDTO judgeDTO = JudgeDTO.builder()
                .testCaseInputContent(testJudgeDTO.getTestCaseInput() + "\n")
                .maxOutputSize(maxOutputSize)
                .testCaseOutputContent(testJudgeDTO.getExpectedOutput())
                .build();

        FutureTask<JSONObject> testJudgeFutureTask = new FutureTask<>(() -> testJudge.judge(judgeDTO, judgeGlobalDTO));

        JSONObject judgeRes = SubmitTask2ThreadPool(testJudgeFutureTask);
        return TestJudgeResult.builder()
                .status(judgeRes.getInt("status"))
                .memory(judgeRes.getLong("memory"))
                .time(judgeRes.getLong("time"))
                .stdout(judgeRes.getStr("output"))
                .stderr(judgeRes.getStr("errMsg"))
                .build();
    }

    public List<JSONObject> judgeAllCase(Long submitId,
                                         Problem problem,
                                         String judgeLanguage,
                                         String testCasesDir,
                                         JSONObject testCasesInfo,
                                         String userFileId,
                                         String userFileContent,
                                         Boolean getUserOutput,
                                         String judgeCaseMode)
            throws JudgeSystemError, ExecutionException, InterruptedException {

        if (testCasesInfo == null) {
            throw new JudgeSystemError("The evaluation data of the problem does not exist", null, null);
        }

        JSONArray testcaseList = (JSONArray) testCasesInfo.get("testCases");

        // 默认给题目限制时间+200ms用来测评
        Long testTime = (long) problem.getTimeLimit() + 200;

        JudgeMode judgeMode = JudgeMode.getJudgeMode(problem.getJudgeMode());

        if (judgeMode == null) {
            throw new RuntimeException("The judge mode of problem " + problem.getProblemId() + " error:" + problem.getJudgeMode());
        }

        // 用户输出的文件夹
        String runDir = OJFiles.getSubmitFolder(submitId);

        LanguageConfig runConfig = LanguageManager.getLanguageConfigByName(judgeLanguage);
        LanguageConfig spjConfig = LanguageManager.getLanguageConfigByName("SPJ-" + problem.getSpjLanguage());
        LanguageConfig interactiveConfig = LanguageManager.getLanguageConfigByName("INTERACTIVE-" + problem.getSpjLanguage());

        final AbstractJudge abstractJudge = getAbstractJudge(judgeMode);

        JudgeGlobalDTO judgeGlobalDTO = JudgeGlobalDTO.builder()
                .problemId(problem.getId())
                .judgeMode(judgeMode)
                .userFileId(userFileId)
                .userFileContent(userFileContent)
                .runDir(runDir)
                .testTime(testTime)
                .maxMemory((long) problem.getMemoryLimit())
                .maxTime((long) problem.getTimeLimit())
                .maxStack(problem.getStackLimit())
                .testCaseInfo(testCasesInfo)
                .judgeExtraFiles(JudgeUtils.getProblemExtraFileMap(problem, "judge"))
                .runConfig(runConfig)
                .spjRunConfig(spjConfig)
                .interactiveRunConfig(interactiveConfig)
                .needUserOutputFile(getUserOutput)
                .removeEOLBlank(problem.getIsRemoveEndBlank())
                .isFileIO(problem.getIsFileIO())
                .ioReadFileName(problem.getIoReadFileName())
                .ioWriteFileName(problem.getIoWriteFileName())
                .build();


        // OI题的subtask最低分模式，则每个subtask组只要有一个case非AC 或者 percentage为 0.0则该组剩余评测点跳过，不再评测。
//        if (ProblemType.isOIType(problem.getType()) && JudgeCaseMode.SUBTASK_LOWEST.getMode().equals(judgeCaseMode)) {
//            return subtaskJudgeAllCase(testcaseList, testCasesDir, judgeGlobalDTO, abstractJudge);
//        } else if (JudgeCaseMode.ERGODIC_WITHOUT_ERROR.getMode().equals(judgeCaseMode)){
//            // 顺序评测测试点，遇到非AC就停止！
//            return ergodicJudgeAllCase(testcaseList, testCasesDir, judgeGlobalDTO, abstractJudge);
//        } else {
            return defaultJudgeAllCase(testcaseList, testCasesDir, judgeGlobalDTO, abstractJudge);
//        }
    }

    /**
     * 默认会评测全部的测试点数据。
     * @param testcaseList
     * @param testCasesDir
     * @param judgeGlobalDTO
     * @param abstractJudge
     */
    private List<JSONObject> defaultJudgeAllCase(JSONArray testcaseList,
                                                 String testCasesDir,
                                                 JudgeGlobalDTO judgeGlobalDTO,
                                                 AbstractJudge abstractJudge) throws ExecutionException, InterruptedException {
        List<FutureTask<JSONObject>> futureTasks = new ArrayList<>();
        for (int index = 0; index < testcaseList.size(); index++) {
            JSONObject testcase = (JSONObject) testcaseList.get(index);
            // 将每个需要测试的线程任务加入任务列表中
            final int testCaseId = index + 1;
            // 输入文件名
            final String inputFileName = testcase.getStr("inputName");
            // 输出文件名
            final String outputFileName = testcase.getStr("outputName");
            // 题目数据的输入文件的路径
            final String testCaseInputPath = OJFiles.join(testCasesDir, inputFileName);
            // 题目数据的输出文件的路径
            final String testCaseOutputPath = OJFiles.join(testCasesDir, outputFileName);
            // 数据库表的测试样例id
            final Long caseId = testcase.getLong("caseId", null);
            // 该测试点的满分
            final Integer score = testcase.getInt("score", 0);
            // 该测试点的分组（用于subtask）
            final Integer groupNum = testcase.getInt("groupNum", 1);

            final Long maxOutputSize = Math.max(testcase.getLong("outputSize", 0L) * 2, 32 * 1024 * 1024L);

            JudgeDTO judgeDTO = JudgeDTO.builder()
                    .testCaseNum(testCaseId)
                    .testCaseInputFileName(inputFileName)
                    .testCaseInputPath(testCaseInputPath)
                    .testCaseOutputFileName(outputFileName)
                    .testCaseOutputPath(testCaseOutputPath)
                    .maxOutputSize(maxOutputSize)
                    .score(score)
                    .build();

            futureTasks.add(new FutureTask<>(() -> {
                JSONObject result = abstractJudge.judge(judgeDTO, judgeGlobalDTO);
                result.set("caseId", caseId);
                result.set("score", judgeDTO.getScore());
                result.set("inputFileName", judgeDTO.getTestCaseInputFileName());
                result.set("outputFileName", judgeDTO.getTestCaseOutputFileName());
                result.set("groupNum", groupNum);
                result.set("seq", testCaseId);
                return result;
            }));

        }
        return SubmitBatchTask2ThreadPool(futureTasks);
    }

    private JSONObject SubmitTask2ThreadPool(FutureTask<JSONObject> futureTask)
            throws InterruptedException, ExecutionException {
        // 提交到线程池进行执行
        ThreadPoolUtils.getInstance().getThreadPool().submit(futureTask);
        while (true) {
            if (futureTask.isDone() && !futureTask.isCancelled()) {
                // 获取线程返回结果
                return futureTask.get();
            } else {
                Thread.sleep(10); // 避免CPU高速运转，这里休息10毫秒
            }
        }
    }

    private List<JSONObject> SubmitBatchTask2ThreadPool(List<FutureTask<JSONObject>> futureTasks)
            throws InterruptedException, ExecutionException {
        // 提交到线程池进行执行
        for (FutureTask<JSONObject> futureTask : futureTasks) {
            ThreadPoolUtils.getInstance().getThreadPool().submit(futureTask);
        }
        List<JSONObject> result = new LinkedList<>();
        while (!futureTasks.isEmpty()) {
            Iterator<FutureTask<JSONObject>> iterable = futureTasks.iterator();
            //遍历一遍
            while (iterable.hasNext()) {
                FutureTask<JSONObject> future = iterable.next();
                if (future.isDone() && !future.isCancelled()) {
                    // 获取线程返回结果
                    JSONObject tmp = future.get();
                    result.add(tmp);
                    // 任务完成移除任务
                    iterable.remove();
                } else {
                    Thread.sleep(10); // 避免CPU高速运转，这里休息10毫秒
                }
            }
        }
        return result;
    }


    private AbstractJudge getAbstractJudge(JudgeMode judgeMode) {
        switch (judgeMode) {
            case DEFAULT:
                return defaultJudge;
//            case SPJ:
//                return specialJudge;
//            case INTERACTIVE:
//                return interactiveJudge;
            default:
                throw new RuntimeException("The problem judge mode is error:" + judgeMode);
        }
    }

}
