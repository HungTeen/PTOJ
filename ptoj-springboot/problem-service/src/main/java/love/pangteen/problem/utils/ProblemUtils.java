package love.pangteen.problem.utils;

import cn.hutool.core.lang.Pair;
import love.pangteen.api.enums.JudgeCaseMode;
import love.pangteen.api.constant.OJConstant;
import love.pangteen.api.pojo.entity.ProblemCase;
import love.pangteen.problem.pojo.entity.TagClassification;
import love.pangteen.problem.pojo.vo.ProblemTagVO;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/23 16:05
 **/
public class ProblemUtils {

    public static final Comparator<ProblemTagVO> PROBLEM_TAG_VO_COMPARATOR = (p1, p2) -> {
        if (p1 == null || p2 == null || p1.getClassification() == null) {
            return 1;
        }
        if (p2.getClassification() == null) {
            return -1;
        }
        TagClassification p1Classification = p1.getClassification();
        TagClassification p2Classification = p2.getClassification();
        if (Objects.equals(p1Classification.getOj(), p2Classification.getOj())) {
            return p1Classification.getRank().compareTo(p2Classification.getRank());
        } else {
            if (OJConstant.DEFAULT_TAG_SOURCE.equals(p1Classification.getOj())) {
                return -1;
            } else if (OJConstant.DEFAULT_TAG_SOURCE.equals(p2Classification.getOj())) {
                return 1;
            } else {
                return p1Classification.getOj().compareTo(p2Classification.getOj());
            }
        }
    };

    public static boolean forAllProblem(String oj){
        return oj != null && !"ALL".equalsIgnoreCase(oj);
    }

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
