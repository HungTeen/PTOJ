package love.pangteen.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import love.pangteen.user.pojo.entity.UserAcProblem;

import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/2/28 9:34
 **/
public interface UserAcProblemService extends IService<UserAcProblem> {
    List<UserAcProblem> getAcProblemList(String uuid);
}
