package love.pangteen.config;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaHttpMethod;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import lombok.extern.slf4j.Slf4j;
import love.pangteen.api.utils.RoleUtils;
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
                            });

                    this.checkUserService();
                    this.checkProblemService();
                    this.checkTrainingService();

                }).setError(e -> {
                    if (e instanceof NotLoginException) {
                        return SaResult.error(e.getMessage()).setCode(HttpStatus.UNAUTHORIZED.value());
                    }
                    return SaResult.error("认证失败，无法访问系统资源").setCode(HttpStatus.UNAUTHORIZED.value());
                });
    }

    /**
     * 用户服务鉴权。
     */
    private void checkUserService(){
        SaRouter.match("/admin/user/**", "/admin/account/logout")
                .check(r -> {
                    StpUtil.checkRoleOr(RoleUtils.getAdmins());
                });

        SaRouter.match("/admin/user/generate-user-excel")
                .check(r -> {
                    StpUtil.checkRole(RoleUtils.getRoot());
                });
    }

    /**
     * 题目服务鉴权。
     */
    private void checkProblemService(){
        SaRouter.match("/admin/problem/**")
                .check(r -> {
                    StpUtil.checkRoleOr(RoleUtils.getAdmins());
                });

        SaRouter.match("/admin/problem")
                .match(SaHttpMethod.DELETE)
                .check(r -> {
                    StpUtil.checkRoleOr(RoleUtils.getProblemAdmins());
                });

        SaRouter.match("/admin/tag/**")
                .check(r -> {
                    StpUtil.checkRoleOr(RoleUtils.getProblemAdmins());
                });
    }

    /**
     * 题单服务鉴权。
     */
    private void checkTrainingService(){
        SaRouter.match("/admin/training/**")
                .check(r -> {
                    StpUtil.checkRoleOr(RoleUtils.getAdmins());
                });

        // Root权限才能删除题单。
        SaRouter.match("/admin/training")
                .match(SaHttpMethod.DELETE)
                .check(r -> {
                    StpUtil.checkRoleOr(RoleUtils.getRoot());
                });

        SaRouter.match("/admin/training/problem", "/admin/training/category/**")
                .check(r -> {
                    StpUtil.checkRoleOr(RoleUtils.getProblemAdmins());
                });
    }
}
