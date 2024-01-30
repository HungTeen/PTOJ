package love.pangteen.submission.publisher;

import lombok.extern.slf4j.Slf4j;
import love.pangteen.api.constant.MQConstants;
import love.pangteen.api.message.SubmissionMessage;
import love.pangteen.submission.service.JudgeService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/30 9:11
 **/
@Slf4j
@Component
public class SubmissionPublisher {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private JudgeService judgeService;

    public void sendTask(Long judgeId, Long pid, Boolean isContest) {
        SubmissionMessage message = new SubmissionMessage(judgeId, isContest);
        // 优先处理比赛的提交任务，其次处理普通提交的提交任务。
        rabbitTemplate.convertAndSend(MQConstants.JUDGE_WAITING_QUEUE, message, msg -> {
            msg.getMessageProperties().setPriority(isContest ? 10 : 5);
            return msg;
        });

        // TODO 失败处理。

//        try {
//            boolean isOk;
//            if (isContest) {
//                isOk = redisUtils.llPush(Constants.Queue.CONTEST_JUDGE_WAITING.getName(), JSONUtil.toJsonStr(task));
//            } else {
//                isOk = redisUtils.llPush(Constants.Queue.GENERAL_JUDGE_WAITING.getName(), JSONUtil.toJsonStr(task));
//            }
//            if (!isOk) {
//                judgeEntityService.updateById(new Judge()
//                        .setSubmitId(judgeId)
//                        .setStatus(Constants.Judge.STATUS_SUBMITTED_FAILED.getStatus())
//                        .setErrorMessage("Call Redis to push task error. Please try to submit again!")
//                );
//            }
//            // 调用判题任务处理
//            judgeReceiver.processWaitingTask();
//        } catch (Exception e) {
//            log.error("调用redis将判题纳入判题等待队列异常--------------->{}", e.getMessage());
//            judgeEntityService.failToUseRedisPublishJudge(judgeId, pid, isContest);
//        }
    }
}
