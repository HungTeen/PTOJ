package love.pangteen.user.controller;

import love.pangteen.result.CommonResult;
import love.pangteen.user.pojo.dto.LoginDTO;
import love.pangteen.user.pojo.vo.UserInfoVO;
import love.pangteen.user.service.AccountService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/15 13:27
 **/
@RestController
@RequestMapping("/admin")
public class AdminAccountController {

    @Resource
    private AccountService accountService;

    @PostMapping("/login")
    public CommonResult<UserInfoVO> login(@Validated @RequestBody LoginDTO loginDto) {
        return CommonResult.success(accountService.login(loginDto));
    }

    @GetMapping("/logout")
    public CommonResult<Void> logout() {
        accountService.logout();
        return CommonResult.success();
    }
}