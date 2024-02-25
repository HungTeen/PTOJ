package love.pangteen.submission.service.impl;

import io.micrometer.core.lang.Nullable;
import love.pangteen.api.pojo.entity.Judge;
import love.pangteen.api.service.IDubboJudgeService;
import love.pangteen.submission.service.JudgeService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/30 10:50
 **/
@DubboService
public class DubboJudgeService implements IDubboJudgeService {

    @Resource
    private JudgeService judgeService;

    @Override
    public List<Judge> getSubmitJudges(List<Long> pidList, String uid, @Nullable Long cid, @Nullable Long gid) {
        return judgeService.lambdaQuery()
                .eq(Judge::getUid, uid)
                .eq(cid != null, Judge::getCid, cid)
                .eq(gid != null, Judge::getGid, gid)
                .isNull(gid == null, Judge::getGid)
                .in(Judge::getPid, pidList)
                .orderByDesc(Judge::getSubmitTime)
                .list();
    }

}
