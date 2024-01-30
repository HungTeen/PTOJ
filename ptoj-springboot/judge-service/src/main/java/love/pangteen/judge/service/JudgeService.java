package love.pangteen.judge.service;

import com.baomidou.mybatisplus.extension.service.IService;
import love.pangteen.api.pojo.dto.ToJudgeDTO;
import love.pangteen.api.pojo.entity.Judge;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/30 11:12
 **/
public interface JudgeService extends IService<Judge> {

    void judge(Judge judge);

    void remoteJudge(ToJudgeDTO toJudgeDTO);
}
