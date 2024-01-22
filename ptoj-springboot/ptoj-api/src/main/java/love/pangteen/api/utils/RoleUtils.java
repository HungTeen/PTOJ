package love.pangteen.api.utils;

import love.pangteen.api.enums.OJRole;

import java.util.Arrays;
import java.util.Collection;
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

    public static String getRoot(){
        return OJRole.ROOT.getRoleName();
    }

    public static String[] getProblemAdmins(){
        return new String[]{getRoot(), OJRole.PROBLEM_ADMIN.getRoleName()};
    }

    public static String[] getAdmins(){
        return getAdminRoles().map(OJRole::getRoleName).toArray(String[]::new);
    }

    public static Stream<OJRole> getAdminRoles(){
        return Arrays.stream(OJRole.values()).filter(OJRole::isAdmin);
    }

}
