package love.pangteen.utils;

import cn.dev33.satoken.stp.StpUtil;
import love.pangteen.api.constant.SessionKeys;
import love.pangteen.pojo.AccountProfile;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/23 15:33
 **/
public class AccountUtils {

    public static void setProfile(AccountProfile accountProfile) {
        StpUtil.getSession().set(SessionKeys.ACCOUNT_PROFILE, accountProfile);
    }

    public static AccountProfile getProfile() {
        return (AccountProfile) StpUtil.getSession().get(SessionKeys.ACCOUNT_PROFILE);
    }

}
