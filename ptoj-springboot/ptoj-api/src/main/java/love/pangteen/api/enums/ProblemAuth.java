package love.pangteen.api.enums;

import lombok.Getter;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/22 10:15
 **/
@Getter
public enum ProblemAuth {

    PUBLIC(1),

    PRIVATE(2),

    CONTEST(3),

    ;

    private final Integer auth;

    ProblemAuth(Integer auth) {
        this.auth = auth;
    }

    public static ProblemAuth getProblemAuth(int auth) {
        for (ProblemAuth problemAuth : ProblemAuth.values()) {
            if (problemAuth.getAuth().equals(auth)) {
                return problemAuth;
            }
        }
        return null;
    }

}