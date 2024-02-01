package love.pangteen.judge.service;


import love.pangteen.judge.pojo.vo.JudgeServerInfoVO;
import love.pangteen.judge.pojo.vo.SystemConfigVO;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/25 15:19
 **/
public interface OJService {
    JudgeServerInfoVO getJudgeServerInfo();

    SystemConfigVO getSystemConfig();
}
