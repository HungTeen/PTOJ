package love.pangteen.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import love.pangteen.user.pojo.dto.AdminEditUserDTO;
import love.pangteen.user.pojo.dto.DeleteUserDTO;
import love.pangteen.user.pojo.dto.GenerateUserDTO;
import love.pangteen.user.pojo.entity.UserInfo;
import love.pangteen.user.pojo.vo.GenerateKeyVO;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/18 18:13
 **/
public interface UserInfoService extends IService<UserInfo> {

    UserInfo getUserInfoByUid(String uid);

    UserInfo getUserInfoByName(String username);

    void editUser(AdminEditUserDTO adminEditUserDto);

    GenerateKeyVO generateUser(GenerateUserDTO generateUserDTO);

    void generateUserExcel(String key, HttpServletResponse response) throws IOException;

    void deleteUser(DeleteUserDTO deleteUserDTO);
}
