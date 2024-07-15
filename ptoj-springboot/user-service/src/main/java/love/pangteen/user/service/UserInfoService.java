package love.pangteen.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import love.pangteen.user.pojo.dto.*;
import love.pangteen.api.pojo.entity.UserInfo;
import love.pangteen.user.pojo.vo.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/18 18:13
 **/
public interface UserInfoService extends IService<UserInfo> {

    UserInfo getUserInfo(String uid);

    UserInfo getUserInfo(String uid, boolean cached);

    UserInfo getUserInfoByName(String username);

    void onUserInfoChanged(String uid);

    boolean editUser(AdminEditUserDTO adminEditUserDto);

    GenerateKeyVO generateUser(GenerateUserDTO generateUserDTO);

    void generateUserExcel(String key, HttpServletResponse response) throws IOException;

    boolean deleteUser(DeleteUserDTO deleteUserDTO);

    CheckUsernameOrEmailVO checkUsernameOrEmail(CheckUsernameOrEmailDTO checkUsernameOrEmailDto);

    int getTotalUserCount();

}
