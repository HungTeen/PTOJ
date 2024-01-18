package love.pangteen.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import love.pangteen.user.mapper.UserRoleMapper;
import love.pangteen.user.pojo.entity.Role;
import love.pangteen.user.pojo.entity.UserRole;
import love.pangteen.user.service.UserRoleService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/18 18:14
 **/
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

    @Override
    public List<Role> getUserRoles(String uid) {
        return getBaseMapper().getRolesByUid(uid);
    }
}
