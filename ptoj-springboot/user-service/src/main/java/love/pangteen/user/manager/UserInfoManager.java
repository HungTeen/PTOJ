package love.pangteen.user.manager;

import love.pangteen.api.pojo.entity.UserInfo;
import love.pangteen.utils.RedisUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 维护一个HashMap，记录 用户ID->用户信息 的映射。
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/7/10 12:45
 **/
@Component
public class UserInfoManager {

    private static final String KEY = "user:info_map";

    @Resource
    private RedisUtils redisUtils;

    public UserInfo getUserInfoFromCache(String uid){
        updateExpire();
        return (UserInfo) redisUtils.hmGet(KEY, uid);
    }

    public void update(String uid, UserInfo info){
        updateExpire();
        redisUtils.hmPut(KEY, uid, info);
    }

    public void onModified(List<String> uids){
        onModified(uids.toArray(new String[0]));
    }

    public void onModified(String... uid){
        updateExpire();
        redisUtils.hmDel(KEY, uid);
    }

    private void updateExpire(){
        redisUtils.expire(KEY, 10000);
    }

}
