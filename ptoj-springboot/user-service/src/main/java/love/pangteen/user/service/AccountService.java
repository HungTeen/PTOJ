package love.pangteen.user.service;

import love.pangteen.result.CommonResult;
import love.pangteen.user.pojo.dto.LoginDTO;
import love.pangteen.user.pojo.vo.UserInfoVO;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/17 20:00
 **/
public interface AccountService {

    CommonResult<UserInfoVO> login(LoginDTO loginDto);

    CommonResult<Void> logout();
}
