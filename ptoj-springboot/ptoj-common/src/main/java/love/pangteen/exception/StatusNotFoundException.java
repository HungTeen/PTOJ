package love.pangteen.exception;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/26 10:27
 **/
public class StatusNotFoundException extends RuntimeException {

    public StatusNotFoundException(String message) {
        super(message);
    }
}
