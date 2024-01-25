package love.pangteen.judge.pojo.vo;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/25 15:12
 **/
@Data
@Builder
public class JudgeServerInfoVO {

    String version;

    Date currentTime;

    String judgeServerName;

    int cpu;

    List<String> languages;

    int maxTaskNum;

    boolean isOpenRemoteJudge;

    int remoteJudgeMaxTaskNum;

    Object SandBoxMsg;

}
