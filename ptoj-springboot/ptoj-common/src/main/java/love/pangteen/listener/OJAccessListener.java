package love.pangteen.listener;

import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/26 14:35
 **/
@Component
public class OJAccessListener implements ApplicationListener<EnvironmentChangeEvent> {

    @Override
    public void onApplicationEvent(EnvironmentChangeEvent event) {
        //TODO 测试配置变更的监听。
    }

}
