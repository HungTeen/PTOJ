package love.pangteen.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/17 19:38
 **/
@Getter
@AllArgsConstructor
public enum ResultStatus {

    SUCCESS(200, "成功"),

    BAD_REQUEST(400, "请求失败"),

    ACCESS_DENIED(401, "未经授权"),

    FORBIDDEN(403, "拒绝访问"),

    NOT_FOUND(404, "数据不存在"),

    SERVER_ERROR(500, "服务器错误"),

    NOT_IMPLEMENTED(501, "功能不支持");


    private final int status;

    private final String description;
}
