package love.pangteen.user.controller;

import love.pangteen.result.CommonResult;
import love.pangteen.user.pojo.dto.CheckUsernameOrEmailDTO;
import love.pangteen.user.pojo.vo.CheckUsernameOrEmailVO;
import love.pangteen.user.service.AccountService;
import love.pangteen.user.service.UserInfoService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/19 9:36
 **/
@RestController
@RequestMapping
public class AccountController {

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private AccountService accountService;

    @GetMapping("/file/generate-user-excel")
    public void generateUserExcel(@RequestParam("key") String key, HttpServletResponse response) throws IOException {
        userInfoService.generateUserExcel(key, response);
    }

    @RequestMapping(value = "/check-username-or-email", method = RequestMethod.POST)
    public CommonResult<CheckUsernameOrEmailVO> checkUsernameOrEmail(@RequestBody CheckUsernameOrEmailDTO checkUsernameOrEmailDto) {
        return CommonResult.success(userInfoService.checkUsernameOrEmail(checkUsernameOrEmailDto));
    }

//    @GetMapping("/get-user-home-info")
//    public CommonResult<UserHomeVO> getUserHomeInfo(@RequestParam(value = "uid", required = false) String uid,
//                                                    @RequestParam(value = "username", required = false) String username) {
//        return CommonResult.success(accountService.getUserHomeInfo(uid, username));
//    }
//
//    @GetMapping("/get-user-calendar-heatmap")
//    public CommonResult<UserCalendarHeatmapVO> getUserCalendarHeatmap(@RequestParam(value = "uid", required = false) String uid,
//                                                                      @RequestParam(value = "username", required = false) String username) {
//        return CommonResult.success(accountService.getUserCalendarHeatmap(uid, username));
//    }
//
//    @PostMapping("/change-password")
//    public CommonResult<ChangeAccountVO> changePassword(@RequestBody ChangePasswordDTO changePasswordDto) {
//        return CommonResult.success(accountService.changePassword(changePasswordDto));
//    }
//
//    @GetMapping("/get-change-email-code")
//    public CommonResult<Void> getChangeEmailCode(@RequestParam("email") String email) {
//        accountService.getChangeEmailCode(email);
//        return CommonResult.success();
//    }
//
//    @PostMapping("/change-email")
//    public CommonResult<ChangeAccountVO> changeEmail(@RequestBody ChangeEmailDTO changeEmailDto) {
//        return CommonResult.success(accountService.changeEmail(changeEmailDto));
//    }
//
//    @PostMapping("/change-userInfo")
//    public CommonResult<UserInfoVO> changeUserInfo(@RequestBody UserInfoVO userInfoVo) {
//        return CommonResult.success(accountService.changeUserInfo(userInfoVo));
//    }
//
//    @GetMapping("/get-user-auth-info")
//    public CommonResult<UserAuthInfoVO> getUserAuthInfo() {
//        return CommonResult.success(accountService.getUserAuthInfo());
//    }

}
