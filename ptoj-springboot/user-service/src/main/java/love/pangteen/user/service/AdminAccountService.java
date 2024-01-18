package love.pangteen.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import love.pangteen.result.CommonResult;
import love.pangteen.user.pojo.dto.LoginDTO;
import love.pangteen.user.pojo.vo.UserInfoVO;
import org.springframework.stereotype.Service;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/17 20:00
 **/
public interface AdminAccountService {

    CommonResult<UserInfoVO> login(LoginDTO loginDto);

    CommonResult<Void> logout();
}
