package love.pangteen.user.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import love.pangteen.user.mapper.UserRoleMapper;
import love.pangteen.user.pojo.entity.Role;
import love.pangteen.user.pojo.vo.UserRolesVO;
import love.pangteen.user.service.UserRoleService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/18 18:14
 **/
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, Role> implements UserRoleService {

    @Override
    public List<Role> getUserRoles(Long roleId) {
        return List.of(getById(roleId));
    }

    @Override
    public List<String> getUserRoles(String uid) {
        return getBaseMapper().getRoles(uid);
    }

    @Override
    public IPage<UserRolesVO> getUserList(Integer limit, Integer currentPage, String keyword, Boolean onlyAdmin) {
        if (currentPage == null || currentPage < 1) currentPage = 1;
        if (limit == null || limit < 1) limit = 10;
        if (keyword != null) {
            keyword = keyword.trim();
        }
        // TODO 角色改成bitmap。
        Page<UserRolesVO> page = new Page<>(currentPage, limit);
        if(onlyAdmin){
            return getBaseMapper().getAdminUserList(page, limit, currentPage, keyword);
        }
        return getBaseMapper().getUserList(page, limit, currentPage, keyword);
    }

}
