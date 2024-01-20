package love.pangteen;

import love.pangteen.api.service.IUserService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/20 17:50
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class DubboTest {

    @DubboReference
    private IUserService userService;

    @org.junit.Test
    public void test(){
//        System.out.println(userService.test());
    }

}
