package love.pangteen.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import love.pangteen.user.pojo.dto.*;
import love.pangteen.user.pojo.entity.UserInfo;
import love.pangteen.user.pojo.vo.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/18 18:13
 **/
public interface UserInfoService extends IService<UserInfo> {

    UserInfo getUserInfoByName(String username);

    void editUser(AdminEditUserDTO adminEditUserDto);

    GenerateKeyVO generateUser(GenerateUserDTO generateUserDTO);

    void generateUserExcel(String key, HttpServletResponse response) throws IOException;

    void deleteUser(DeleteUserDTO deleteUserDTO);

    CheckUsernameOrEmailVO checkUsernameOrEmail(CheckUsernameOrEmailDTO checkUsernameOrEmailDto);

    UserInfoVO changeUserInfo(EditUserInfoDTO editUserInfoDTO);

    UserHomeVO getUserHomeInfo(String uid, String username);

    UserCalendarHeatmapVO getUserCalendarHeatmap(String uid, String username);
}
