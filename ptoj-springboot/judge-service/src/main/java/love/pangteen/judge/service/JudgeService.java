package love.pangteen.judge.service;

import com.baomidou.mybatisplus.extension.service.IService;
import love.pangteen.api.enums.JudgeStatus;
import love.pangteen.api.pojo.dto.TestJudgeDTO;
import love.pangteen.api.pojo.dto.ToJudgeDTO;
import love.pangteen.api.pojo.entity.Judge;
import love.pangteen.api.pojo.entity.TestJudgeResult;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/30 11:12
 **/
public interface JudgeService extends IService<Judge> {

    JudgeStatus judge(Judge judge);

    void remoteJudge(ToJudgeDTO toJudgeDTO);

    TestJudgeResult testJudge(TestJudgeDTO testJudgeDTO);

    int getUserAcceptCount(String uid);
}
