package love.pangteen.api.enums;

import lombok.Getter;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/22 10:17
 **/
@Getter
public enum JudgeMode {

    TEST("test"),

    DEFAULT("default"),

    SPJ("spj"),

    INTERACTIVE("interactive"),

    ;

    private final String mode;

    JudgeMode(String mode) {
        this.mode = mode;
    }

    public static JudgeMode getJudgeMode(String mode) {
        for (JudgeMode judgeMode : JudgeMode.values()) {
            if (judgeMode.getMode().equals(mode)) {
                return judgeMode;
            }
        }
        return null;
    }
}
