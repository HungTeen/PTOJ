package love.pangteen.api.constant;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/22 15:42
 **/
public interface MQConstants {

    /* Exchange */

    String JUDGE_EXCHANGE = "judge.direct";

    /* Queue */

    String JUDGE_WAITING_QUEUE = "direct.judge.queue";
    String CONTEST_REMOTE_JUDGE_WAITING_QUEUE = "direct.judge.remote.contest.queue";
    String GENERAL_REMOTE_JUDGE_WAITING_QUEUE = "direct.judge.remote.general.queue";

    /* Routing Key */

    String CONTEST = "contest";
    String GENERAL = "general";
    String CONTEST_REMOTE = "contest.remote";
    String GENERAL_REMOTE = "general.remote";

    /* Topic */

    String SUBMIT_TOPIC = "submit";
    String JUDGE_TOPIC = "judge";
    String ACCEPT_TOPIC = "accept";

    String JUDGE_CONSUMER_GROUP = "ONLINE_JUDGE";
    String ACCEPT_GROUP = "ACCEPT_GROUP";

}
