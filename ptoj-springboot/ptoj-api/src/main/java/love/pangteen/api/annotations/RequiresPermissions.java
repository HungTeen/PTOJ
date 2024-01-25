package love.pangteen.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 单纯做个标记，方便寻找需要权限验证的方法。
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/25 16:31
 **/
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresPermissions {

    String[] value() default {};
}
