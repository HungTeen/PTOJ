package love.pangteen.user.manager;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.SecureUtil;
import love.pangteen.exception.StatusAccessDeniedException;
import love.pangteen.exception.StatusFailException;
import love.pangteen.user.pojo.dto.LoginDTO;
import love.pangteen.user.pojo.entity.Role;
import love.pangteen.user.pojo.entity.UserInfo;
import love.pangteen.user.pojo.vo.UserInfoVO;
import love.pangteen.user.pojo.vo.UserRolesVO;
import love.pangteen.user.service.UserInfoService;
import love.pangteen.user.service.UserRoleService;
import love.pangteen.user.utils.JwtUtils;
import love.pangteen.user.utils.RoleUtils;
import love.pangteen.utils.RedisUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/18 12:35
 **/
public class AdminAccountManager {

    @Resource
    private JwtUtils jwtUtils;
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private UserInfoService userInfoService;
    @Resource
    private UserRoleService userRoleService;

    public UserInfoVO login(LoginDTO loginDto) throws StatusFailException, StatusAccessDeniedException {

        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        HttpServletResponse response = servletRequestAttributes.getResponse();

//        String userIpAddr = IpUtils.getUserIpAddr(request);
//        String key = Constants.Account.TRY_LOGIN_NUM.getCode() + loginDto.getUsername() + "_" + userIpAddr;
//        Integer tryLoginCount = (Integer) redisUtils.get(key);
//
//        if (tryLoginCount != null && tryLoginCount >= 20) {
//            throw new StatusFailException("对不起！登录失败次数过多！您的账号有风险，半个小时内暂时无法登录！");
//        }

        // 根据用户名查找用户。
        UserInfo userInfo = userInfoService.getUserInfoByName(loginDto.getUsername());
        if (userInfo == null) {
            throw new StatusFailException("用户名或密码错误");
        }

        if (!userInfo.getPassword().equals(SecureUtil.md5(loginDto.getPassword()))) {
//            if (tryLoginCount == null) {
//                redisUtils.set(key, 1, 60 * 30); // 三十分钟不尝试，该限制会自动清空消失
//            } else {
//                redisUtils.set(key, tryLoginCount + 1, 60 * 30);
//            }
            throw new StatusFailException("用户名或密码错误");
        }

        if (userInfo.getStatus() != 0) {
            throw new StatusFailException("该账户已被封禁，请联系管理员进行处理！");
        }

        // 认证成功，清除锁定限制
//        if (tryLoginCount != null) {
//            redisUtils.del(key);
//        }

        // 查询用户角色
        List<String> roles = userRoleService.getUserRoles(userInfo.getUuid()).stream()
                .map(Role::getRole).collect(Collectors.toList());

        // 是管理员。
        if (RoleUtils.hasAdminRole(roles)) {
            String jwt = jwtUtils.generateToken(userInfo.getUuid());

            response.setHeader("Authorization", jwt); //放到信息头部
            response.setHeader("Access-Control-Expose-Headers", "Authorization");
//            // 会话记录
//            sessionEntityService.save(new Session().setUid(userRolesVo.getUid())
//                    .setIp(IpUtils.getUserIpAddr(request)).setUserAgent(request.getHeader("User-Agent")));
//            // 异步检查是否异地登录
//            sessionEntityService.checkRemoteLogin(userRolesVo.getUid());

            UserInfoVO userInfoVo = new UserInfoVO();
            BeanUtil.copyProperties(userInfo, userInfoVo);
            userInfoVo.setRoleList(roles);

            return userInfoVo;
        } else {
            throw new StatusAccessDeniedException("该账号并非管理员账号，无权登录！");
        }
    }
}
