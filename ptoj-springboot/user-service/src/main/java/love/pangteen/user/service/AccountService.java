package love.pangteen.user.service;

import love.pangteen.user.pojo.dto.ChangeEmailDTO;
import love.pangteen.user.pojo.dto.ChangePasswordDTO;
import love.pangteen.user.pojo.dto.LoginDTO;
import love.pangteen.user.pojo.vo.*;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/24 16:23
 **/
public interface AccountService {

    UserInfoVO login(LoginDTO loginDto);

    void logout();

    UserHomeVO getUserHomeInfo(String uid, String username);

    UserCalendarHeatmapVO getUserCalendarHeatmap(String uid, String username);

    ChangeAccountVO changePassword(ChangePasswordDTO changePasswordDto);

    void getChangeEmailCode(String email);

    ChangeAccountVO changeEmail(ChangeEmailDTO changeEmailDto);

    UserInfoVO changeUserInfo(UserInfoVO userInfoVo);

    UserAuthInfoVO getUserAuthInfo();
}
