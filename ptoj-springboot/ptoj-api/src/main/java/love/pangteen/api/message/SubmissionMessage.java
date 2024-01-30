package love.pangteen.api.message;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/30 9:37
 **/
@Data
@RequiredArgsConstructor
public class SubmissionMessage {

    private final Long judgeId;

    private final Boolean isContest;
}
