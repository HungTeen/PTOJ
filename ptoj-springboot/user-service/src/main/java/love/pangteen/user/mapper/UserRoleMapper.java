package love.pangteen.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import love.pangteen.user.pojo.entity.Role;
import love.pangteen.user.pojo.vo.UserRolesVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/18 18:06
 **/
public interface UserRoleMapper extends BaseMapper<Role> {

    @Select("SELECT r.role FROM user_info u, role r WHERE u.role_id = r.id AND u.uuid = #{uid}")
    List<String> getRoles(String uid);

    UserRolesVO getUserRoles(@Param("uid") String uid, @Param("username") String username);

    IPage<UserRolesVO> getUserList(Page<UserRolesVO> page, @Param("limit") int limit,
                                   @Param("currentPage") int currentPage,
                                   @Param("keyword") String keyword);

    IPage<UserRolesVO> getAdminUserList(Page<UserRolesVO> page, @Param("limit") int limit,
                                        @Param("currentPage") int currentPage,
                                        @Param("keyword") String keyword);
}
