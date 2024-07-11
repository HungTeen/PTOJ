package love.pangteen.user.service.impl;

import love.pangteen.api.pojo.entity.UserInfo;
import love.pangteen.api.service.IDubboUserService;
import love.pangteen.user.service.UserInfoService;
import love.pangteen.user.service.UserRoleService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/20 16:43
 **/
@DubboService
public class DubboUserServiceImpl implements IDubboUserService {

    @Resource
    private UserRoleService userRoleService;

    @Resource
    private UserInfoService userInfoService;

    @Override
    public List<String> getUserRoles(String uid) {
        return userRoleService.getUserRoles(uid);
    }

    @Override
    public List<UserInfo> getAllUsers() {
        return userInfoService.list();
    }
}
