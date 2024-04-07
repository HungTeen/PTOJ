package love.pangteen.api.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/3/27 13:17
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmitMessage {

    private Long pid;

    private String uid;

    private Date submitDate;

}
