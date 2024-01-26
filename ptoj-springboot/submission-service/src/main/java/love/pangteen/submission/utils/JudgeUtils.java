package love.pangteen.submission.utils;

import love.pangteen.api.enums.JudgeStatus;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/26 10:45
 **/
public class JudgeUtils {

    /**
     * 只允许用户查看ce错误,sf错误，se错误信息提示。
     */
    public static boolean canSeeErrorMsg(int judgeStatus){
        return judgeStatus != JudgeStatus.STATUS_COMPILE_ERROR.getStatus() &&
                judgeStatus != JudgeStatus.STATUS_SYSTEM_ERROR.getStatus() &&
                judgeStatus != JudgeStatus.STATUS_SUBMITTED_FAILED.getStatus();
    }

    public static boolean isContestSubmission(Long contestId){
        return contestId != null && contestId != 0;
    }

    public static boolean isTrainingSubmission(Long trainingId){
        return trainingId != null && trainingId != 0;
    }

}
