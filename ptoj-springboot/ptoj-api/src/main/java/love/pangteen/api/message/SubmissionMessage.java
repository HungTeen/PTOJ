package love.pangteen.api.message;

import lombok.Data;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/30 9:37
 **/
@Data
public class SubmissionMessage {

    private Long judgeId;

    private Boolean isContest;
}
