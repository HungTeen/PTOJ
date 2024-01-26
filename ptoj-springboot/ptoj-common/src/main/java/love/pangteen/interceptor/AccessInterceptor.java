package love.pangteen.interceptor;

import cn.hutool.core.annotation.AnnotationUtil;
import love.pangteen.api.annotations.OJAccess;
import love.pangteen.api.enums.OJAccessType;
import love.pangteen.utils.AccessUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/26 11:23
 **/
@Component
public class AccessInterceptor implements HandlerInterceptor {

    @Resource
    private AccessUtils accessUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 静态资源的请求不处理
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            OJAccess ojAccess = getAnnotation(handlerMethod);
            if (ojAccess == null) {
                return true;
            }
            for (OJAccessType value : ojAccess.value()) {
                accessUtils.validateAccess(value);
            }
            return true;
        } else return handler instanceof ResourceHttpRequestHandler;
    }

    private OJAccess getAnnotation(HandlerMethod handlerMethod) {
        OJAccess ojAccess = AnnotationUtil.getAnnotation(handlerMethod.getMethod(), OJAccess.class);
        if (ojAccess == null) {
            ojAccess = AnnotationUtil.getAnnotation(handlerMethod.getBeanType(), OJAccess.class);
        }
        return ojAccess;
    }
}
