package love.pangteen.problem.utils;

import cn.hutool.core.lang.Pair;
import love.pangteen.api.enums.JudgeCaseMode;
import love.pangteen.problem.pojo.entity.ProblemCase;

import java.util.HashMap;
import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/23 16:05
 **/
public class ProblemUtils {

    public static int calProblemTotalScore(String judgeCaseMode, List<ProblemCase> problemCaseList) {
        int sumScore = 0;
        if (JudgeCaseMode.SUBTASK_LOWEST.getMode().equals(judgeCaseMode)) {
            HashMap<Integer, Integer> groupNumMapScore = new HashMap<>();
            for (ProblemCase problemCase : problemCaseList) {
                groupNumMapScore.merge(problemCase.getGroupNum(), problemCase.getScore(), Math::min);
            }
            for (Integer minScore : groupNumMapScore.values()) {
                sumScore += minScore;
            }
        } else if (JudgeCaseMode.SUBTASK_AVERAGE.getMode().equals(judgeCaseMode)) {
            // 预处理 切换成Map Key: groupNum Value: <count,sum_score>
            HashMap<Integer, Pair<Integer, Integer>> groupNumMapScore = new HashMap<>();
            for (ProblemCase problemCase : problemCaseList) {
                Pair<Integer, Integer> pair = groupNumMapScore.get(problemCase.getGroupNum());
                if (pair == null) {
                    groupNumMapScore.put(problemCase.getGroupNum(), new Pair<>(1, problemCase.getScore()));
                } else {
                    int count = pair.getKey() + 1;
                    int score = pair.getValue() + problemCase.getScore();
                    groupNumMapScore.put(problemCase.getGroupNum(), new Pair<>(count, score));
                }
            }
            for (Pair<Integer, Integer> pair : groupNumMapScore.values()) {
                sumScore += (int) Math.round(pair.getValue() * 1.0 / pair.getKey());
            }
        } else {
            for (ProblemCase problemCase : problemCaseList) {
                if (problemCase.getScore() != null) {
                    sumScore += problemCase.getScore();
                }
            }
        }
        return sumScore;
    }
}
