package love.pangteen.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import love.pangteen.user.pojo.entity.Role;
import love.pangteen.user.pojo.entity.UserRole;
import love.pangteen.user.pojo.vo.UserRolesVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/18 18:06
 **/
public interface UserRoleMapper extends BaseMapper<UserRole> {

    @Select("SELECT r.* FROM user_role ur LEFT JOIN role r ON ur.role_id = r.id WHERE ur.uid = #{uid}")
    List<Role> getRolesByUid(String uid);

//    IPage<UserRolesVO> getUserList(Page<UserRolesVO> page, @Param("limit") int limit,
//                                   @Param("currentPage") int currentPage,
//                                   @Param("keyword") String keyword);
//
//    IPage<UserRolesVO> getAdminUserList(Page<UserRolesVO> page, @Param("limit") int limit,
//                                        @Param("currentPage") int currentPage,
//                                        @Param("keyword") String keyword);
}
