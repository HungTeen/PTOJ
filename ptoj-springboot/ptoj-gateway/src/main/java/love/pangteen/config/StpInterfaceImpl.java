package love.pangteen.config;

import cn.dev33.satoken.stp.StpInterface;
import love.pangteen.api.service.IDubboUserService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/20 15:19
 **/
@Component
public class StpInterfaceImpl implements StpInterface {

    @DubboReference
    private IDubboUserService userService;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return List.of();
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        if(loginId instanceof String){
            List<String> roles = userService.getUserRoles((String) loginId);
            return roles;
        }
        return List.of();
    }

}
