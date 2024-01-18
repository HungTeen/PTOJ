package love.pangteen.user.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import love.pangteen.user.constant.ShiroConstant;
import love.pangteen.utils.RedisUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/17 21:43
 **/
public class JwtUtils {

    @Resource
    private JwtProperties jwtProperties;

    @Resource
    private RedisUtils redisUtils;

    /**
     * 生成jwt token
     */
    public String generateToken(String userId) {
        Date nowDate = new Date();
        //过期时间
        Date expireDate = new Date(nowDate.getTime() + jwtProperties.getExpire() * 1000);

        String token = Jwts.builder()
                .setHeaderParam("type", "JWT")
                .setSubject(userId)
                .setIssuedAt(nowDate)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, jwtProperties.getSecret())
                .compact();
        redisUtils.set(shiroTokenKey(userId), token, jwtProperties.getExpire());
        redisUtils.set(shiroTokenRefresh(userId), "1", jwtProperties.getCheckRefreshExpire());
        return token;
    }

    public void cleanToken(String userId) {
        redisUtils.del(shiroTokenKey(userId), shiroTokenRefresh(userId));
    }

    public boolean hasToken(String userId) {
        return redisUtils.hasKey(shiroTokenKey(userId));
    }

    private static String shiroTokenKey(String userId){
        return ShiroConstant.SHIRO_TOKEN_KEY + userId;
    }

    private static String shiroTokenRefresh(String userId){
        return ShiroConstant.SHIRO_TOKEN_REFRESH + userId;
    }

    @Data
    @Component
    @ConfigurationProperties(prefix = "hoj.jwt")
    public static class JwtProperties {

        private String secret;

        private long expire;

        private String header;

        private long checkRefreshExpire;

    }
}
