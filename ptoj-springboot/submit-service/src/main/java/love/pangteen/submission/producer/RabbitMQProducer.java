package love.pangteen.submission.producer;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/30 9:11
 **/
//@Slf4j
//@Component
public class RabbitMQProducer {

//    @Resource
//    private RabbitTemplate rabbitTemplate;
//
//    @Resource
//    private JudgeService judgeService;
//
//    public void sendTask(SubmissionMessage message, boolean isContest) {
//        // 优先处理比赛的提交任务，其次处理普通提交的提交任务。
//        rabbitTemplate.convertAndSend(MQConstants.JUDGE_WAITING_QUEUE, message, msg -> {
//            msg.getMessageProperties().setPriority(isContest ? 2 : 1);
//            return msg;
//        });
//
////        try {
////            boolean isOk;
////            if (isContest) {
////                isOk = redisUtils.llPush(Constants.Queue.CONTEST_JUDGE_WAITING.getName(), JSONUtil.toJsonStr(task));
////            } else {
////                isOk = redisUtils.llPush(Constants.Queue.GENERAL_JUDGE_WAITING.getName(), JSONUtil.toJsonStr(task));
////            }
////            if (!isOk) {
////                judgeEntityService.updateById(new Judge()
////                        .setSubmitId(judgeId)
////                        .setStatus(Constants.Judge.STATUS_SUBMITTED_FAILED.getStatus())
////                        .setErrorMessage("Call Redis to push task error. Please try to submit again!")
////                );
////            }
////            // 调用判题任务处理
////            judgeReceiver.processWaitingTask();
////        } catch (Exception e) {
////            log.error("调用redis将判题纳入判题等待队列异常--------------->{}", e.getMessage());
////            judgeEntityService.failToUseRedisPublishJudge(judgeId, pid, isContest);
////        }
//    }
}
