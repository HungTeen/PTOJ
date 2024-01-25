package love.pangteen.user.controller;

import love.pangteen.api.annotations.IgnoreLogin;
import love.pangteen.result.CommonResult;
import love.pangteen.user.pojo.dto.CheckUsernameOrEmailDTO;
import love.pangteen.user.pojo.dto.EditUserInfoDTO;
import love.pangteen.user.pojo.vo.CheckUsernameOrEmailVO;
import love.pangteen.user.pojo.vo.UserInfoVO;
import love.pangteen.user.service.UserInfoService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/15 13:24
 **/
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserInfoService userInfoService;

    @RequestMapping(value = "/check-username-or-email", method = RequestMethod.POST)
    @IgnoreLogin
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

    /**
     * 用户修改个人信息。
     */
    @PostMapping("/change-userinfo")
    public CommonResult<UserInfoVO> changeUserInfo(@Validated @RequestBody EditUserInfoDTO editUserInfoDTO) {
        return CommonResult.success(userInfoService.changeUserInfo(editUserInfoDTO));
    }

}
