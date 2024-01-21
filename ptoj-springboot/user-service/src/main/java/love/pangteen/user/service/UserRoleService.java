package love.pangteen.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import love.pangteen.user.pojo.entity.Role;
import love.pangteen.user.pojo.vo.UserRolesVO;

import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/18 18:13
 **/
public interface UserRoleService extends IService<Role> {

    /**
     * 别问为什么是List。
     */
    List<Role> getUserRoles(Long roleId);

    /**
     * 根据用户uid，查询角色名。
     */
    List<String> getUserRoles(String uid);

    IPage<UserRolesVO> getUserList(Integer limit, Integer currentPage, String keyword, Boolean onlyAdmin);
//
//    void deleteCache(String uid, boolean isRemoveSession);
//
//    String getAuthChangeContent(int oldType,int newType);
}
