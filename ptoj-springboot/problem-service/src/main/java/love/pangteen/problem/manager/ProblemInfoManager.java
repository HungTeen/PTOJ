package love.pangteen.problem.manager;

import love.pangteen.problem.pojo.vo.RecentUpdatedProblemVO;
import love.pangteen.utils.RedisUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/7/11 16:57
 **/
public class ProblemInfoManager {

    private static final String KEY = "problem:info_map";

    @Resource
    private RedisUtils redisUtils;

    public RecentUpdatedProblemVO getProblemInfoFromCache(Long uid){
        updateExpire();
        return (RecentUpdatedProblemVO) redisUtils.hmGet(KEY, uid);
    }

    public void update(Long uid, RecentUpdatedProblemVO info){
        updateExpire();
        redisUtils.hmPut(KEY, uid, info);
    }

    public void onModified(List<Long> pids){
        onModified(pids.toArray(new Long[0]));
    }

    public void onModified(Long... pid){
        updateExpire();
        redisUtils.hmDel(KEY, pid);
    }

    private void updateExpire(){
        redisUtils.expire(KEY, 10000);
    }

}
