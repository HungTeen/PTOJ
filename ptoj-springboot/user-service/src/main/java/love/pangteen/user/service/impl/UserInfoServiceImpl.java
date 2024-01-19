package love.pangteen.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import love.pangteen.user.mapper.UserInfoMapper;
import love.pangteen.user.mapper.UserRoleMapper;
import love.pangteen.user.pojo.entity.UserInfo;
import love.pangteen.user.service.UserInfoService;
import org.springframework.stereotype.Service;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/18 22:30
 **/
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {


    @Override
    public UserInfo getUserInfoByUid(String uid) {
        return getById(uid);
    }

    @Override
    public UserInfo getUserInfoByName(String username) {
        return lambdaQuery().eq(UserInfo::getUsername, username).one();
    }

}
