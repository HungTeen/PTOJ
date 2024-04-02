package love.pangteen.api.service;

import cn.hutool.core.lang.Pair;
import love.pangteen.api.pojo.entity.Judge;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/30 10:50
 **/
public interface IDubboJudgeService {

    /**
     * 获取用户对题目的提交通过map。
     * @param uuid 用户uuid。
     * @param pidSet 题目id集合。
     * @return 每个题目是否通过。
     */
    HashMap<Long, Boolean> getUserAcceptCount(String uuid, Collection<Long> pidSet);

    /**
     * 获取题目的ac和提交数。
     * @param pid 题目id。
     * @return Pair<总提交数, 通过提交数>。
     */
    Pair<Integer, Integer> getAcStats(Long pid);

    int getUserTotalSubmitCount(String uuid);

    List<Judge> getLastYearUserJudgeList(String uid);

    int getTodayJudgeCount();

    List<Long> getUserAcceptList(String uid);

    List<Pair<String, Long>> getAcceptList();
}
