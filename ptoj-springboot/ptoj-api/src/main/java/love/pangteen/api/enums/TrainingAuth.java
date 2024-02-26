package love.pangteen.api.enums;

import lombok.Getter;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/2/26 15:00
 **/
@Getter
public enum TrainingAuth {

    AUTH_PRIVATE("Private"),

    AUTH_PUBLIC("Public"),

    ;

    private final String value;

    TrainingAuth(String value) {
        this.value = value;
    }

    public static boolean isPrivate(String auth) {
        return AUTH_PRIVATE.getValue().equals(auth);
    }

}
