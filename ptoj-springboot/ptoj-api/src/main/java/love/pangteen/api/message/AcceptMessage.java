package love.pangteen.api.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/3/27 13:37
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AcceptMessage {

    private String uid;

    private Long pid;

    private Long acCount;

}
