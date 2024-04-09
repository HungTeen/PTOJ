package love.pangteen.manager;

import love.pangteen.api.pojo.entity.TestJudgeContext;
import love.pangteen.api.pojo.entity.TestJudgeResult;
import love.pangteen.utils.RedisUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/4/9 23:46
 **/
@Component
public class TestJudgeContentManager {

    @Resource
    private RedisUtils redisUtils;

    public void saveTestJudgeContext(String key, TestJudgeContext value) {
        redisUtils.set(contextKey(key), value, 300);
    }

    public void saveTestJudgeResult(String key, TestJudgeResult value) {
        redisUtils.set(resultKey(key), value, 120);
    }

    public TestJudgeContext getTestJudgeContext(String key) {
        return (TestJudgeContext) redisUtils.get(contextKey(key));
    }

    public TestJudgeResult getTestJudgeResult(String key) {
        return (TestJudgeResult) redisUtils.get(resultKey(key));
    }

    public void delTestJudgeContext(String key) {
        redisUtils.del(contextKey(key));
    }

    public void delTestJudgeResult(String key) {
        redisUtils.del(resultKey(key));
    }

    public String contextKey(String key){
        return "test_judge/context/" + key;
    }

    public String resultKey(String key){
        return "test_judge/result/" + key;
    }

}
