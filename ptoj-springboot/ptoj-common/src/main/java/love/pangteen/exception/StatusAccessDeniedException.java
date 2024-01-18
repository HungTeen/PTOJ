package love.pangteen.exception;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/18 12:45
 **/
public class StatusAccessDeniedException extends RuntimeException {

    public StatusAccessDeniedException(String message) {
        super(message);
    }
}
