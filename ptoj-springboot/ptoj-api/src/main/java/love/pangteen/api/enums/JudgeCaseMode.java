package love.pangteen.api.enums;

import lombok.Getter;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/22 10:18
 **/
@Getter
public enum JudgeCaseMode {

    DEFAULT("default"),

    SUBTASK_LOWEST("subtask_lowest"),

    SUBTASK_AVERAGE("subtask_average"),

    ERGODIC_WITHOUT_ERROR("ergodic_without_error"),

    ;

    private final String mode;

    JudgeCaseMode(String mode) {
        this.mode = mode;
    }

    public static JudgeCaseMode getJudgeCaseMode(String mode) {
        for (JudgeCaseMode judgeCaseMode : JudgeCaseMode.values()) {
            if (judgeCaseMode.getMode().equals(mode)) {
                return judgeCaseMode;
            }
        }
        return DEFAULT;
    }

    public static boolean hasGroup(String judgeCaseMode){
        return judgeCaseMode.equals(JudgeCaseMode.SUBTASK_AVERAGE.getMode()) || judgeCaseMode.equals(JudgeCaseMode.SUBTASK_LOWEST.getMode());
    }
}
