package love.pangteen.user.service.impl;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.crypto.SecureUtil;
import love.pangteen.api.utils.RoleUtils;
import love.pangteen.exception.StatusAccessDeniedException;
import love.pangteen.exception.StatusFailException;
import love.pangteen.exception.StatusSystemErrorException;
import love.pangteen.pojo.AccountProfile;
import love.pangteen.user.pojo.dto.ChangeEmailDTO;
import love.pangteen.user.pojo.dto.ChangePasswordDTO;
import love.pangteen.user.pojo.dto.LoginDTO;
import love.pangteen.user.pojo.entity.Role;
import love.pangteen.api.pojo.entity.UserInfo;
import love.pangteen.user.pojo.vo.ChangeAccountVO;
import love.pangteen.user.pojo.vo.UserAuthInfoVO;
import love.pangteen.user.pojo.vo.UserInfoVO;
import love.pangteen.user.service.AccountService;
import love.pangteen.user.service.UserInfoService;
import love.pangteen.user.service.UserRoleService;
import love.pangteen.utils.AccountUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/24 16:23
 **/
@Service
public class AccountServiceImpl implements AccountService {

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private UserRoleService userRoleService;

    @Transactional
    @Override
    public UserInfoVO login(LoginDTO loginDto, boolean requireAdmin, HttpServletResponse response, HttpServletRequest request) {
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
            throw new StatusFailException("用户名或密码错误！请注意大小写！");
        }

        if (!userInfo.getPassword().equals(SaSecureUtil.md5(loginDto.getPassword()))) {
//            if (tryLoginCount == null) {
//                redisUtils.set(key, 1, 60 * 30); // 三十分钟不尝试，该限制会自动清空消失
//            } else {
//                redisUtils.set(key, tryLoginCount + 1, 60 * 30);
//            }
            throw new StatusFailException("用户名或密码错误！请注意大小写！");
        }

        if (userInfo.getStatus() != 0) {
            throw new StatusFailException("该账户已被封禁，请联系管理员进行处理！");
        }

        // 认证成功，清除锁定限制
//        if (tryLoginCount != null) {
//            redisUtils.del(key);
//        }

        // 查询用户角色
        List<String> roles = userRoleService.getUserRoles(userInfo.getRoleId()).stream()
                .map(Role::getRole).collect(Collectors.toList());

        if (! requireAdmin || RoleUtils.hasAdminRole(roles)) {
            // 登录并缓存用户信息到Session。
            StpUtil.login(userInfo.getUuid());
            AccountProfile profile = new AccountProfile();
            BeanUtils.copyProperties(userInfo, profile);
            AccountUtils.setProfile(profile);

            response.setHeader("Authorization", StpUtil.getTokenValue()); //放到信息头部
            response.setHeader("Access-Control-Expose-Headers", "Authorization");
//            // 会话记录
//            sessionEntityService.save(new Session().setUid(userRolesVo.getUid())
//                    .setIp(IpUtils.getUserIpAddr(request)).setUserAgent(request.getHeader("User-Agent")));
//            // 异步检查是否异地登录
//            sessionEntityService.checkRemoteLogin(userRolesVo.getUid());

            UserInfoVO userInfoVo = new UserInfoVO();
            BeanUtils.copyProperties(userInfo, userInfoVo);
            userInfoVo.setRoleList(roles);

            return userInfoVo;
        } else {
            throw new StatusAccessDeniedException("该账号并非管理员账号，无权登录！");
        }
    }

    @Override
    public void logout() {
        StpUtil.logout();
    }

    @Override
    public ChangeAccountVO changePassword(ChangePasswordDTO changePasswordDto) {
//        // 如果已经被锁定半小时，则不能修改
//        String lockKey = Constants.Account.CODE_CHANGE_PASSWORD_LOCK + userRolesVo.getUid();
//        // 统计失败的key
//        String countKey = Constants.Account.CODE_CHANGE_PASSWORD_FAIL + userRolesVo.getUid();
//
//        ChangeAccountVO resp = new ChangeAccountVO();
//        if (redisUtils.hasKey(lockKey)) {
//            long expire = redisUtils.getExpire(lockKey);
//            Date now = new Date();
//            long minute = expire / 60;
//            long second = expire % 60;
//            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            resp.setCode(403);
//            Date afterDate = new Date(now.getTime() + expire * 1000);
//            String msg = "由于您多次修改密码失败，修改密码功能已锁定，请在" + minute + "分" + second + "秒后(" + formatter.format(afterDate) + ")再进行尝试！";
//            resp.setMsg(msg);
//            return resp;
//        }
        AccountProfile profile = AccountUtils.getProfile();
        ChangeAccountVO resp = new ChangeAccountVO();
        UserInfo userInfo = userInfoService.getById(profile.getUuid());
        // 与当前登录用户的密码进行比较判断
        if (userInfo.getPassword().equals(SecureUtil.md5(changePasswordDto.getOldPassword()))) { // 如果相同，则进行修改密码操作
            if (userInfoService.lambdaUpdate()
                    .eq(UserInfo::getUuid, profile.getUuid())
                    .set(UserInfo::getPassword, SecureUtil.md5(changePasswordDto.getNewPassword()))
                    .update()) {
                resp.setCode(200);
                resp.setMsg("修改密码成功！您将于5秒钟后退出进行重新登录操作！");
//                // 清空记录
//                redisUtils.del(countKey);
                return resp;
            } else {
                throw new StatusSystemErrorException("系统错误：修改密码失败！");
            }
        } else { // 如果不同，则进行记录，当失败次数达到5次，半个小时后才可重试
//            Integer count = (Integer) redisUtils.get(countKey);
//            if (count == null) {
//                redisUtils.set(countKey, 1, 60 * 30); // 三十分钟不尝试，该限制会自动清空消失
//                count = 0;
//            } else if (count < 5) {
//                redisUtils.incr(countKey, 1);
//            }
//            count++;
//            if (count == 5) {
//                redisUtils.del(countKey); // 清空统计
//                redisUtils.set(lockKey, "lock", 60 * 30); // 设置锁定更改
//            }
            resp.setCode(400);
            resp.setMsg("原始密码错误！");//您已累计修改密码失败" + count + "次...");
            return resp;
        }
    }

    @Override
    public void getChangeEmailCode(String email) {

    }

    @Override
    public ChangeAccountVO changeEmail(ChangeEmailDTO changeEmailDto) {
        return null;
    }

    @Override
    public UserAuthInfoVO getUserAuthInfo() {
        UserAuthInfoVO userAuthInfoVO = new UserAuthInfoVO();
        userAuthInfoVO.setRoles(userRoleService.getUserRoles(StpUtil.getLoginIdAsString()));
        return userAuthInfoVO;
    }

}
