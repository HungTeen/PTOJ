package love.pangteen.user.manager;

import cn.hutool.core.lang.Pair;
import lombok.extern.slf4j.Slf4j;
import love.pangteen.api.service.IDubboJudgeService;
import love.pangteen.utils.RedisUtils;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/3/27 21:49
 **/
@Slf4j
@Component
public class UserAcceptManager {

    private static final int USER_COUNT = 10;
    private static final String KEY = "user:accept:board";
    private static final String RANK_LOCK = "lock:rank";

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private IDubboJudgeService dubboJudgeService;

    void assertInit(){
        if(! initialized()){
            redisUtils.lock(RANK_LOCK);
            try{
                if(! initialized()){
                    List<Pair<String, Long>> userList = dubboJudgeService.getAcceptList();
                    userList.forEach(pair -> {
                        updateUserCount(pair.getKey(), pair.getValue(), false);
                    });
                }
            } catch (Exception ignored){
                log.error(ignored.getMessage());
            } finally {
                redisUtils.unlock(RANK_LOCK);
            }
        } else {
            redisUtils.expire(KEY, 10000);
        }
    }

    public void updateUserCount(String uid, Long count, boolean check){
        if(check) assertInit();
        if(redisUtils.zContains(KEY, uid)){
            redisUtils.zRemove(KEY, uid);
        }
        redisUtils.zAdd(KEY, uid, - count);
    }

    public List<ZSetOperations.TypedTuple<String>> getTopUsers(){
        assertInit();
        return redisUtils.zRange(KEY, 0, USER_COUNT).stream().map(tuple -> {
            return ZSetOperations.TypedTuple.of((String) tuple.getValue(), - tuple.getScore());
        }).collect(Collectors.toList());
    }

    public boolean initialized(){
        return redisUtils.hasKey(KEY);
    }

}
