package love.pangteen.exception;

import lombok.Data;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/25 15:31
 **/
@Data
public class JudgeSystemError extends Exception {
    private String message;
    private String stdout;
    private String stderr;

    public JudgeSystemError(String message, String stdout, String stderr) {
        super(message + " " + stderr);
        this.message = message;
        this.stdout = stdout;
        this.stderr = stderr;
    }
}
