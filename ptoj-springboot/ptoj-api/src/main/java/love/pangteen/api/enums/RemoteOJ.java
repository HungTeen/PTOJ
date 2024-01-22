package love.pangteen.api.enums;

import lombok.Getter;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/22 9:10
 **/
@Getter
public enum RemoteOJ {

    HDU("HDU"),

    CODEFORCES("CF"),

    GYM("GYM"),

    POJ("POJ"),

    SPOJ("SPOJ"),

    ATCODER("AC"),

    ;

    private final String name;

    RemoteOJ(String name) {
        this.name = name;
    }

    public static boolean isRemoteOJ(String name) {
        for (RemoteOJ remoteOJ : RemoteOJ.values()) {
            if (remoteOJ.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

}
