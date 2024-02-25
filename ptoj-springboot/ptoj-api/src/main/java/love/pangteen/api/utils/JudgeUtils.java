package love.pangteen.api.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import love.pangteen.api.enums.JudgeStatus;
import love.pangteen.api.pojo.entity.Problem;

import java.util.*;

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
        return judgeStatus == JudgeStatus.STATUS_COMPILE_ERROR.getStatus() ||
                judgeStatus == JudgeStatus.STATUS_SYSTEM_ERROR.getStatus() ||
                judgeStatus == JudgeStatus.STATUS_RUNTIME_ERROR.getStatus() ||
                judgeStatus == JudgeStatus.STATUS_SUBMITTED_FAILED.getStatus();
    }

    public static boolean isContestSubmission(Long contestId){
        return contestId != null && contestId != 0;
    }

    public static boolean isTrainingSubmission(Long trainingId){
        return trainingId != null && trainingId != 0;
    }

    public static boolean isAccepted(Integer status){
        return JudgeStatus.STATUS_ACCEPTED.getStatus().equals(status);
    }

    public static HashMap<String, String> getProblemExtraFileMap(Problem problem, String type) {
        if ("user".equals(type)) {
            if (!StrUtil.isEmpty(problem.getUserExtraFile())) {
                return (HashMap<String, String>) JSONUtil.toBean(problem.getUserExtraFile(), Map.class);
            }
        } else if ("judge".equals(type)) {
            if (!StrUtil.isEmpty(problem.getJudgeExtraFile())) {
                return (HashMap<String, String>) JSONUtil.toBean(problem.getJudgeExtraFile(), Map.class);
            }
        }
        return null;
    }

    public static List<String> translateCommandline(String toProcess) {
        if (toProcess != null && !toProcess.isEmpty()) {
            int state = 0;
            StringTokenizer tok = new StringTokenizer(toProcess, "\"' ", true);
            List<String> result = new ArrayList<>();
            StringBuilder current = new StringBuilder();
            boolean lastTokenHasBeenQuoted = false;

            while (true) {
                while (tok.hasMoreTokens()) {
                    String nextTok = tok.nextToken();
                    switch (state) {
                        case 1:
                            if ("'".equals(nextTok)) {
                                lastTokenHasBeenQuoted = true;
                                state = 0;
                            } else {
                                current.append(nextTok);
                            }
                            continue;
                        case 2:
                            if ("\"".equals(nextTok)) {
                                lastTokenHasBeenQuoted = true;
                                state = 0;
                            } else {
                                current.append(nextTok);
                            }
                            continue;
                    }

                    if ("'".equals(nextTok)) {
                        state = 1;
                    } else if ("\"".equals(nextTok)) {
                        state = 2;
                    } else if (" ".equals(nextTok)) {
                        if (lastTokenHasBeenQuoted || current.length() > 0) {
                            result.add(current.toString());
                            current.setLength(0);
                        }
                    } else {
                        current.append(nextTok);
                    }

                    lastTokenHasBeenQuoted = false;
                }

                if (lastTokenHasBeenQuoted || current.length() > 0) {
                    result.add(current.toString());
                }

                if (state != 1 && state != 2) {
                    return result;
                }

                throw new RuntimeException("unbalanced quotes in " + toProcess);
            }
        } else {
            return new ArrayList<>();
        }
    }

}
