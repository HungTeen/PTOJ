package love.pangteen.result;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/17 19:36
 **/
@Data
public class CommonResult<T> implements Serializable {

    private final Integer status; // 状态码
    private final T data; // 数据。
    private final String msg; // 自定义信息。

    public static <T> CommonResult<T> create(ResultStatus status, T data, String msg) {
        return new CommonResult<>(status.getStatus(), data, msg);
    }

    /* 成功 */

    public static <T> CommonResult<T> success(T data, String msg) {
        return create(ResultStatus.SUCCESS, data, msg);
    }

    public static <T> CommonResult<T> success(T data) {
        return success(data, "success");
    }

    public static <T> CommonResult<T> success() {
        return successMsg("success");
    }

    public static <T> CommonResult<T> successMsg(String msg) {
        return success(null, msg);
    }

    /* 失败 */

    public static <T> CommonResult<T> error(ResultStatus resultStatus, String msg) {
        return create(resultStatus, null, msg);
    }

    public static <T> CommonResult<T> error(ResultStatus resultStatus) {
        return error(resultStatus, resultStatus.getDescription());
    }

    public static <T> CommonResult<T> clientError(String msg) {
        return error(ResultStatus.BAD_REQUEST, msg);
    }

    public static <T> CommonResult<T> clientError() {
        return error(ResultStatus.BAD_REQUEST);
    }

    public static <T> CommonResult<T> serverError(String msg) {
        return error(ResultStatus.SERVER_ERROR, msg);
    }

    public static <T> CommonResult<T> serverError() {
        return error(ResultStatus.SERVER_ERROR);
    }

}
