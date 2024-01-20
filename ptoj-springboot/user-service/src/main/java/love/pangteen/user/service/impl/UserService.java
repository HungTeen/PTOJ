package love.pangteen.user.service.impl;

import love.pangteen.api.service.IUserService;
import love.pangteen.user.pojo.entity.Role;
import love.pangteen.user.service.UserRoleService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/20 16:43
 **/
@DubboService
public class UserService implements IUserService {

    @Resource
    private UserRoleService userRoleService;

    @Override
    public List<String> getUserRoles(String uid) {
        return userRoleService.getUserRoles(uid).stream().map(Role::getRole).collect(Collectors.toList());
    }
}
