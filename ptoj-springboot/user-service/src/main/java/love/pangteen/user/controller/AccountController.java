package love.pangteen.user.controller;

import love.pangteen.api.annotations.IgnoreLogin;
import love.pangteen.result.CommonResult;
import love.pangteen.user.pojo.dto.LoginDTO;
import love.pangteen.user.pojo.vo.UserAuthInfoVO;
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
 * @create: 2024/1/19 9:36
 **/
@RestController
@RequestMapping("/account")
public class AccountController {

    @Resource
    private AccountService accountService;

    /**
     * 登录。
     */
    @PostMapping("/login")
    @IgnoreLogin
    public CommonResult<UserInfoVO> login(@Validated @RequestBody LoginDTO loginDto, HttpServletResponse response, HttpServletRequest request) {
        return CommonResult.success(accountService.login(loginDto, false, response, request));
    }

//    /**
//     * 调用邮件服务，发送注册流程的6位随机验证码。
//     */
//    @RequestMapping(value = "/get-register-code", method = RequestMethod.GET)
//    @IgnoreLogin
//    public CommonResult<RegisterCodeVO> getRegisterCode(@RequestParam(value = "email", required = true) String email) {
//        return passportService.getRegisterCode(email);
//    }
//
//
//    /**
//     * 注册。
//     */
//    @PostMapping("/register")
//    @IgnoreLogin
//    public CommonResult<Void> register(@Validated @RequestBody RegisterDTO registerDto) {
//        return passportService.register(registerDto);
//    }
//
//
//    /**
//     * 发送重置密码的链接邮件。
//     */
//    @PostMapping("/apply-reset-password")
//    @IgnoreLogin
//    public CommonResult<Void> applyResetPassword(@RequestBody ApplyResetPasswordDTO applyResetPasswordDto) {
//        return passportService.applyResetPassword(applyResetPasswordDto);
//    }
//
//
//    /**
//     * 用户重置密码。
//     */
//    @PostMapping("/reset-password")
//    @IgnoreLogin
//    public CommonResult<Void> resetPassword(@RequestBody ResetPasswordDTO resetPasswordDto) {
//        return passportService.resetPassword(resetPasswordDto);
//    }


    /**
     * 退出，下次需要再次登录。
     */
    @GetMapping("/logout")
    public CommonResult<Void> logout() {
        accountService.logout();
        return CommonResult.success();
    }

    @GetMapping("/get-user-auth-info")
    public CommonResult<UserAuthInfoVO> getUserAuthInfo() {
        return CommonResult.success(accountService.getUserAuthInfo());
    }

}
