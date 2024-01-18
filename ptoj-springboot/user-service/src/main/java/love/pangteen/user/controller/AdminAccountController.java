package love.pangteen.user.controller;

import love.pangteen.result.CommonResult;
import love.pangteen.user.pojo.dto.LoginDTO;
import love.pangteen.user.pojo.vo.UserInfoVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/15 13:27
 **/
@RestController
@RequestMapping("/admin")
public class AdminAccountController {

    @PostMapping("/login")
    public CommonResult<UserInfoVO> login(@RequestBody LoginDTO loginDto){
        System.out.println("666");
    }
}
