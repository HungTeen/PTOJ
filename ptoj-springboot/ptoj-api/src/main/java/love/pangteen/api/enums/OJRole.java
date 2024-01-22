package love.pangteen.api.enums;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/18 22:56
 **/
public enum OJRole {

    ROOT(true, 1000),

    ADMIN(true, 1001),

    DEFAULT_USER(false, 1002),

    PROBLEM_ADMIN(true, 1008),

    ;

    private final boolean isAdmin;
    private final int roleId;

    OJRole(boolean isAdmin, int roleId) {
        this.isAdmin = isAdmin;
        this.roleId = roleId;
    }

    public String getRoleName(){
        return name().toLowerCase();
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public long getRoleId() {
        return roleId;
    }
}
