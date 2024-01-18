package love.pangteen.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import love.pangteen.user.pojo.entity.UserInfo;
import love.pangteen.user.pojo.entity.UserRole;
import love.pangteen.user.pojo.vo.UserRolesVO;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/18 18:13
 **/
public interface UserInfoService extends IService<UserInfo> {

    UserInfo getUserInfoByUid(String uid);

    UserInfo getUserInfoByName(String username);
}
