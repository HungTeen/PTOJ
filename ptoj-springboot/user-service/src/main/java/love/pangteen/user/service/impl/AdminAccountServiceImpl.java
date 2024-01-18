package love.pangteen.user.service.impl;

import love.pangteen.result.CommonResult;
import love.pangteen.user.pojo.dto.LoginDTO;
import love.pangteen.user.pojo.vo.UserInfoVO;
import love.pangteen.user.service.AdminAccountService;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/17 20:02
 **/
public class AdminAccountServiceImpl implements AdminAccountService {

    @Override
    public CommonResult<UserInfoVO> login(LoginDTO loginDto) {
        return null;
    }

    @Override
    public CommonResult<Void> logout() {
        return null;
    }
}
