package love.pangteen.user.constant;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/18 22:56
 **/
public enum Roles {

    ROOT(true),

    ADMIN(true),

    PROBLEM_ADMIN(true),

    ;

    private final boolean isAdmin;

    Roles(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getRoleName(){
        return name().toLowerCase();
    }

    public boolean isAdmin() {
        return isAdmin;
    }
}
