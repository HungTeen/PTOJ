package love.pangteen.api.annotations;

import love.pangteen.api.enums.OJAccessType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/26 11:20
 **/
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface OJAccess {

    OJAccessType[] value();
}
