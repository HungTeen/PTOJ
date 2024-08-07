package love.pangteen.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/17 22:25
 **/
@Slf4j
@Component
public class RedisUtils {

    private static final long DEFAULT_EXPIRE_UNUSED = 60000L;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private RedisLockRegistry redisLockRegistry;

    /* Common */

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     */
    public void expire(String key, long time) {
        if (time > 0) {
            redisTemplate.expire(key, time, TimeUnit.SECONDS);
        }
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void del(String... key) {
        if (ArrayUtil.isNotEmpty(key)) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(CollUtil.toList(key));
            }
        }
    }

    public void del(Collection<String> keys) {
        if (CollUtil.isNotEmpty(keys)) {
            redisTemplate.delete(keys);
        }
    }

    /* Lock */

    public void lock(String lockKey) {
        Lock lock = obtainLock(lockKey);
        lock.lock();
    }

    public boolean tryLock(String lockKey) {
        Lock lock = obtainLock(lockKey);
        return lock.tryLock();
    }

    public boolean tryLock(String lockKey, long seconds) {
        Lock lock = obtainLock(lockKey);
        try {
            return lock.tryLock(seconds, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            return false;
        }
    }

    public void unlock(String lockKey) {
        try {
            Lock lock = obtainLock(lockKey);
            lock.unlock();
            redisLockRegistry.expireUnusedOlderThan(DEFAULT_EXPIRE_UNUSED);
        } catch (Exception e) {
            log.error("分布式锁 [{}] 释放异常", lockKey, e);
        }
    }

    private Lock obtainLock(String lockKey) {
        return redisLockRegistry.obtain(lockKey);
    }

    /* String */

    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void set(String key, Object value, long time) {
        if (time > 0) {
            redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
        } else {
            set(key, value);
        }
    }

    /* List */

    public void lPushLeft(String key, Object value) {
        redisTemplate.opsForList().leftPush(key, value);
    }

    public void lPop(String key, Object value) {
        redisTemplate.opsForList().remove(key, 1, value);
    }

    public List<Object> lRange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    public boolean lContains(String key, Object value) {
        return redisTemplate.opsForList().indexOf(key, value) != null;
    }

    /* Hash Map */

    public Map<Object, Object> hmGet(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    public Object hmGet(String key, Object k) {
        return redisTemplate.opsForHash().get(key, k);
    }

    public void hmPut(String key, Object k, Object v) {
        redisTemplate.opsForHash().put(key, k, v);
    }

    public void hmPutAll(String key, Map<String, Object> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    public void hmPutAll(String key, Map<String, Object> map, long time) {
        hmPutAll(key, map);
        if (time > 0) {
            expire(key, time);
        }
    }

    public void hmDel(String key, Object ... ks) {
        redisTemplate.opsForHash().delete(key, ks);
    }

    /* ZSet */

    public void zAdd(String key, Object value, double score) {
        redisTemplate.opsForZSet().add(key, value, score);
    }

    public Long zRemove(String key, Object... values) {
        return redisTemplate.opsForZSet().remove(key, values);
    }

    public boolean zContains(String key, Object value) {
        return redisTemplate.opsForZSet().rank(key, value) != null;
    }

    public Set<ZSetOperations.TypedTuple<Object>> zRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().rangeWithScores(key, start, end);
    }


}
