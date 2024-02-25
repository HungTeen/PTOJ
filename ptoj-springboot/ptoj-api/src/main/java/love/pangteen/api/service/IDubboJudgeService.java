package love.pangteen.api.service;

import love.pangteen.api.pojo.entity.Judge;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/30 10:50
 **/
public interface IDubboJudgeService {

    List<Judge> getSubmitJudges(List<Long> pidList, String uid,  @Nullable Long cid, @Nullable Long gid);


}
