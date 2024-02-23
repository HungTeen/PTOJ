package love.pangteen.api.enums;

import lombok.Getter;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/22 15:41
 **/
@Getter
public enum JudgeStatus {

    STATUS_NOT_SUBMITTED(-10, "Not Submitted", null),

    STATUS_SUBMITTED_UNKNOWN_RESULT(-5, "Submitted Unknown Result", null),

    STATUS_CANCELLED(-4, "Cancelled", "ca"),

    STATUS_PRESENTATION_ERROR(-3, "Presentation Error", "pe"),

    STATUS_COMPILE_ERROR(-2, "Compile Error", "ce"),

    STATUS_WRONG_ANSWER(-1, "Wrong Answer", "wa"),

    STATUS_ACCEPTED(0, "Accepted", "ac"),

    STATUS_TIME_LIMIT_EXCEEDED(1, "Time Limit Exceeded", "tle"),

    STATUS_MEMORY_LIMIT_EXCEEDED(2, "Memory Limit Exceeded", "mle"),

    STATUS_RUNTIME_ERROR(3, "Runtime Error", "re"),

    STATUS_SYSTEM_ERROR(4, "System Error", "se"),

    STATUS_PENDING(5, "Pending", null),

    STATUS_COMPILING(6, "Compiling", null),

    STATUS_JUDGING(7, "Judging", null),

    STATUS_PARTIAL_ACCEPTED(8, "Partial Accepted", "pa"),

    STATUS_SUBMITTING(9, "Submitting", null),

    STATUS_SUBMITTED_FAILED(10, "Submitted Failed", null),

    STATUS_NULL(15, "No Status", null),

    JUDGE_SERVER_SUBMIT_PREFIX(-1002, "Judge SubmitId-ServerId:", null),

    ;

    private final Integer status;
    private final String name;
    private final String columnName;

    JudgeStatus(Integer status, String name, String columnName) {
        this.status = status;
        this.name = name;
        this.columnName = columnName;
    }

    public static JudgeStatus getTypeByStatus(int status) {
        for (JudgeStatus judge : JudgeStatus.values()) {
            if (judge.getStatus() == status) {
                return judge;
            }
        }
        return STATUS_NULL;
    }

}
