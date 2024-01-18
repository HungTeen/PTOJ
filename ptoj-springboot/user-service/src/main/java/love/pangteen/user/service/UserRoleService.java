package love.pangteen.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import love.pangteen.user.pojo.entity.Role;
import love.pangteen.user.pojo.entity.UserRole;
import love.pangteen.user.pojo.vo.UserRolesVO;

import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/18 18:13
 **/
public interface UserRoleService extends IService<UserRole> {

    List<Role> getUserRoles(String uid);

//    List<Role> getRolesByUid(String uid);
//
//    IPage<UserRolesVO> getUserList(int limit, int currentPage, String keyword, Boolean onlyAdmin);
//
//    void deleteCache(String uid, boolean isRemoveSession);
//
//    String getAuthChangeContent(int oldType,int newType);
}
