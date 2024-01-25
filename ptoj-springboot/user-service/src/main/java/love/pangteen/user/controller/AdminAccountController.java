package love.pangteen.user.controller;

import love.pangteen.api.annotations.IgnoreLogin;
import love.pangteen.result.CommonResult;
import love.pangteen.user.pojo.dto.LoginDTO;
import love.pangteen.user.pojo.vo.UserInfoVO;
import love.pangteen.user.service.AccountService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/15 13:27
 **/
@RestController
@RequestMapping("/admin/account")
public class AdminAccountController {

    @Resource
    private AccountService accountService;

    @PostMapping("/login")
    @IgnoreLogin
    public CommonResult<UserInfoVO> login(@Validated @RequestBody LoginDTO loginDto, HttpServletResponse response, HttpServletRequest request) {
        return CommonResult.success(accountService.login(loginDto, true, response, request));
    }

    @GetMapping("/logout")
    public CommonResult<Void> logout() {
        accountService.logout();
        return CommonResult.success();
    }
}
