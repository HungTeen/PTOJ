package love.pangteen.api.service;

import love.pangteen.api.pojo.entity.UserInfo;

import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/20 16:30
 **/
public interface IDubboUserService {

    List<String> getUserRoles(String uid);

    List<UserInfo> getAllUsers();

}
