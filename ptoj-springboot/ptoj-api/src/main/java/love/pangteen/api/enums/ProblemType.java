package love.pangteen.api.enums;

import lombok.Getter;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/22 10:12
 **/
@Getter
public enum ProblemType {

    ACM(0),

    OI(1),

    ;

    private final Integer type;

    ProblemType(Integer type) {
        this.type = type;
    }

    public static ProblemType getProblemType(int type) {
        return values()[type];
    }

    public static boolean isOIType(int type) {
        return type == OI.getType();
    }

    public static boolean isACMType(int type) {
        return type == ACM.getType();
    }

}

