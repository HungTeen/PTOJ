package love.pangteen.api.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/3/27 13:37
 **/
@Data
@AllArgsConstructor
public class AcceptMessage {

    private String uid;

    private Long pid;

    private Long acCount;

}
