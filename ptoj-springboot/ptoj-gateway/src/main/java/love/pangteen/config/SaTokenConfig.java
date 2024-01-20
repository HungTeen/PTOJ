package love.pangteen.config;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import lombok.extern.slf4j.Slf4j;
import love.pangteen.config.properties.IgnoreWhiteProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/19 16:48
 **/
@Slf4j
@Configuration
public class SaTokenConfig {

    @Bean
    public SaReactorFilter getSaReactorFilter(IgnoreWhiteProperties ignoreWhiteProperties) {
        return new SaReactorFilter()
                // 拦截地址
                .addInclude("/**")
                .addExclude("/favicon.ico", "/actuator/**")
                // 鉴权方法：每次访问进入
                .setAuth(obj -> {
                    // 登录校验 -- 拦截所有路由
                    SaRouter.match("/**")
                            .notMatch(ignoreWhiteProperties.getWhitelist())
                            .check(r -> {
                                // 检查是否登录 是否有token
                                StpUtil.checkLogin();

//                                // 检查 header 与 param 里的 clientid 与 token 里的是否一致
//                                ServerHttpRequest request = SaReactorSyncHolder.getContext().getRequest();
//                                String headerCid = request.getHeaders().getFirst(LoginHelper.CLIENT_KEY);
//                                String paramCid = request.getQueryParams().getFirst(LoginHelper.CLIENT_KEY);
//                                String clientId = StpUtil.getExtra(LoginHelper.CLIENT_KEY).toString();
//                                if (!StringUtils.equalsAny(clientId, headerCid, paramCid)) {
//                                    // token 无效
//                                    throw NotLoginException.newInstance(StpUtil.getLoginType(),
//                                            "-100", "客户端ID与Token不匹配",
//                                            StpUtil.getTokenValue());
//                                }
                            });
                }).setError(e -> {
                    if (e instanceof NotLoginException) {
                        return SaResult.error(e.getMessage()).setCode(HttpStatus.UNAUTHORIZED.value());
                    }
                    return SaResult.error("认证失败，无法访问系统资源").setCode(HttpStatus.UNAUTHORIZED.value());
                });
//        return new SaReactorFilter()
//                // 拦截地址
//                .addInclude("/**")    /* 拦截全部path */
//                // 开放地址
//                .addExclude("/favicon.ico")
//                // 鉴权方法：每次访问进入
//                .setAuth(obj -> {
//                    // 登录校验 -- 拦截所有路由，并排除/user/doLogin 用于开放登录
//                    SaRouter.match("/**", "/user/doLogin", r -> StpUtil.checkLogin());
//
//                    // 权限认证 -- 不同模块, 校验不同权限
//                    SaRouter.match("/user/**", r -> StpUtil.checkPermission("user"));
//                    SaRouter.match("/admin/**", r -> StpUtil.checkPermission("admin"));
//                    SaRouter.match("/goods/**", r -> StpUtil.checkPermission("goods"));
//                    SaRouter.match("/orders/**", r -> StpUtil.checkPermission("orders"));
//
//                })
//                // 异常处理方法：每次setAuth函数出现异常时进入
//                .setError(e -> {
//                    return SaResult.error(e.getMessage());
//                })
//                ;
    }
}
