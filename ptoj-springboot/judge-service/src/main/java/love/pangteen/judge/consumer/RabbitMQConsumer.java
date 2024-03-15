package love.pangteen.judge.consumer;

//import org.springframework.amqp.core.ExchangeTypes;
//import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/30 9:45
 **/
@Component
public class RabbitMQConsumer {

//    @Resource
//    private JudgeService judgeService;
//
//    @DubboReference
//    private IDubboProblemService problemService;
//
//    @Resource
//    private JudgeManager judgeManager;
//
//    @RabbitListener(bindings = @QueueBinding(
//            value = @Queue(value = MQConstants.JUDGE_WAITING_QUEUE, durable = "true", arguments = {
//                    @Argument(name = "x-max-priority", value = "5", type = "java.lang.Integer")
//            }),
//            exchange = @Exchange(value = MQConstants.JUDGE_EXCHANGE, type = ExchangeTypes.DIRECT, durable = "true"),
//            key = MQConstants.GENERAL
//    ))
//    public void listenGeneralJudgeMsg(SubmissionMessage msg){
//        if(msg.getIsLocalTest()){
//            // 自测代码。
//            Problem problem = problemService.getById(msg.getPid());
//            if(problem != null){
//                TestJudgeDTO testJudgeDTO = new TestJudgeDTO();
//                testJudgeDTO.setMemoryLimit(problem.getMemoryLimit())
//                        .setTimeLimit(problem.getTimeLimit())
//                        .setStackLimit(problem.getStackLimit())
//                        .setCode(msg.getCode())
//                        .setLanguage(msg.getLanguage())
//                        .setUniqueKey(msg.getUniqueKey())
//                        .setExpectedOutput(msg.getExpectedOutput())
//                        .setTestCaseInput(msg.getUserInput())
//                        .setProblemJudgeMode(problem.getJudgeMode())
//                        .setIsRemoveEndBlank(problem.getIsRemoveEndBlank() || problem.getIsRemote())
//                        .setIsFileIO(problem.getIsFileIO())
//                        .setIoReadFileName(problem.getIoReadFileName())
//                        .setIoWriteFileName(problem.getIoWriteFileName());
//                judgeManager.submitProblemTestJudge(testJudgeDTO);
//            }
//        } else {
//            Judge judge = judgeService.getById(msg.getJudgeId());
//            if (judge != null) {
//                // 调度评测时发现该评测任务被取消，则结束评测。
//                if (Objects.equals(judge.getStatus(), JudgeStatus.STATUS_CANCELLED.getStatus())) {
////                if (JudgeUtils.isContestSubmission(judge.getCid())) {
////                    UpdateWrapper<ContestRecord> updateWrapper = new UpdateWrapper<>();
////                    // 取消评测，不罚时也不算得分
////                    updateWrapper.set("status", Constants.Contest.RECORD_NOT_AC_NOT_PENALTY.getCode());
////                    updateWrapper.eq("submit_id", judge.getSubmitId()); // submit_id一定只有一个
////                    contestRecordEntityService.update(updateWrapper);
////                }
//                } else {
//                    // 调用判题服务。
//                    judgeManager.submitProblemJudge(new ToJudgeDTO()
//                            .setJudge(judge)
//                            .setRemoteJudgeProblem(null)
//                    );
//                }
//            }
//        }
//    }

}
