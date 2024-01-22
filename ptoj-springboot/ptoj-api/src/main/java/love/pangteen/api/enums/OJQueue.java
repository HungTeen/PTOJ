package love.pangteen.api.enums;

import lombok.Getter;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/22 15:42
 **/
@Getter
public enum OJQueue {

    CONTEST_JUDGE_WAITING("Contest_Waiting_Handle_Queue"),
    GENERAL_JUDGE_WAITING("General_Waiting_Handle_Queue"),
    TEST_JUDGE_WAITING("Test_Judge_Waiting_Handle_Queue"),
    CONTEST_REMOTE_JUDGE_WAITING_HANDLE("Contest_Remote_Waiting_Handle_Queue"),
    GENERAL_REMOTE_JUDGE_WAITING_HANDLE("General_Remote_Waiting_Handle_Queue");

    private final String name;

    OJQueue(String name) {
        this.name = name;
    }

}
