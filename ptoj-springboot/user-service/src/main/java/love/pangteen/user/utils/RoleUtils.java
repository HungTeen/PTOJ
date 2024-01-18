package love.pangteen.user.utils;

import love.pangteen.user.constant.Roles;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/18 22:55
 **/
public class RoleUtils {

    /**
     * 是超级管理员或管理员、题目管理员。
     */
    public static boolean hasAdminRole(Collection<String> roles){
        return getAdminRoles().anyMatch(role -> roles.contains(role.getRoleName()));
    }

    public static Stream<Roles> getAdminRoles(){
        return Arrays.stream(Roles.values()).filter(Roles::isAdmin);
    }

}
