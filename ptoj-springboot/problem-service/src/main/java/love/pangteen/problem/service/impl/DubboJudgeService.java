package love.pangteen.problem.service.impl;

import cn.hutool.core.lang.Pair;
import love.pangteen.api.enums.JudgeStatus;
import love.pangteen.api.pojo.entity.Judge;
import love.pangteen.api.service.IDubboJudgeService;
import love.pangteen.api.utils.Utils;
import love.pangteen.problem.mapper.JudgeMapper;
import love.pangteen.problem.service.JudgeService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.HashMap;
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

    @Resource
    private JudgeMapper judgeMapper;

    @Override
    public HashMap<Long, Boolean> getUserAcceptCount(String uuid, Collection<Long> pidSet) {
        List<Judge> acList = judgeService.lambdaQuery()
                .select(Judge::getPid)
                .eq(Judge::getStatus, JudgeStatus.STATUS_ACCEPTED.getStatus())
                .eq(Judge::getUid, uuid)
                .in(Judge::getPid, pidSet).list();
        HashMap<Long, Boolean> map = new HashMap<>();
        acList.forEach(j -> {
            map.put(j.getPid(), true);
        });
        return map;
    }

    @Override
    public Pair<Integer, Integer> getAcStats(Long pid) {
        Long total = judgeService.lambdaQuery()
                .eq(Judge::getPid, pid)
                .eq(Judge::getCid, 0)
                .count();
        Long ac = judgeService.lambdaQuery()
                .eq(Judge::getPid, pid)
                .eq(Judge::getCid, 0)
                .eq(Judge::getStatus, JudgeStatus.STATUS_ACCEPTED.getStatus())
                .count();
        return Pair.of(total.intValue(), ac.intValue());
    }

    @Override
    public int getUserTotalSubmitCount(String uuid) {
        return judgeService.lambdaQuery()
                .eq(Judge::getUid, uuid)
                .eq(Judge::getCid, 0)
                .isNull(Judge::getGid)
                .count().intValue();
    }

    @Override
    public List<Judge> getLastYearUserJudgeList(String uuid) {
        return judgeService.lambdaQuery()
                .select(Judge::getSubmitTime)
                .eq(Judge::getUid, uuid)
                .eq(Judge::getCid, 0)
                .ge(Judge::getSubmitTime, Utils.getYearAgo(1))
                .list();
    }

    @Override
    public int getTodayJudgeCount() {
        return Math.toIntExact(judgeService.lambdaQuery()
                .ge(Judge::getSubmitTime, Utils.getDayAgo(1))
                .count());
    }

    @Override
    public List<Long> getUserAcceptList(String uid) {
        return judgeMapper.getUserAcceptList(uid, JudgeStatus.STATUS_ACCEPTED.getStatus());
    }

    @Override
    public List<Pair<String, Long>> getAcceptList() {
        return judgeMapper.getAcceptList(JudgeStatus.STATUS_ACCEPTED.getStatus());
    }

}
