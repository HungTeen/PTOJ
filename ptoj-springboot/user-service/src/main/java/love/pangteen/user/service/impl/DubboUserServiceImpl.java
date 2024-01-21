package love.pangteen.user.service.impl;

import love.pangteen.api.service.IDubboUserService;
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

    @Override
    public List<String> getUserRoles(String uid) {
        return userRoleService.getUserRoles(uid);
    }
}
