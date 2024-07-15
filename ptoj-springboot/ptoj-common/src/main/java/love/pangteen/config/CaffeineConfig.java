package love.pangteen.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import love.pangteen.constant.CacheConstant;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/7/15 12:30
 **/
@Configuration
public class CaffeineConfig {

    @Bean
    public CacheManager cacheManager(){
        CaffeineCacheManager manager = new CaffeineCacheManager();
        Caffeine<Object, Object> caffeine = Caffeine.newBuilder()
                .expireAfterAccess(CacheConstant.EXPIRE_AFTER_ACCESS, TimeUnit.SECONDS)
//                .maximumSize(CacheConstant.MAX_SIZE)
                ;
        manager.setCaffeine(caffeine);
        return manager;
    }
}
