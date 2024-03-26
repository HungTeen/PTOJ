package love.pangteen.interceptor;

import cn.dev33.satoken.same.SaSameUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/3/26 14:46
 **/
@Component
public class RestTemplateInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        // 向头部中加入一些信息
        addRequestHeader(request);
        // restTemplate调用其他请求
        ClientHttpResponse response = execution.execute(request, body);
        // 在返回之前也可以做一些其他的操作，如cookie管理。关于手动管理cookie，后面也会介绍
        return response;
    }

    // 向头部中加入一些信息
    private void addRequestHeader(HttpRequest request) {
        HttpHeaders headers = request.getHeaders();

        // 设置请求类型
        headers.add(SaSameUtil.SAME_TOKEN, SaSameUtil.getToken());
    }

}
