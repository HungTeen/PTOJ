package love.pangteen.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import love.pangteen.user.mapper.UserAcProblemMapper;
import love.pangteen.user.pojo.entity.UserAcProblem;
import love.pangteen.user.service.UserAcProblemService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/2/28 9:36
 **/
@Service
public class UserAcProblemServiceImpl extends ServiceImpl<UserAcProblemMapper, UserAcProblem> implements UserAcProblemService {


    @Override
    public List<UserAcProblem> getAcProblemList(String uuid) {
        return lambdaQuery()
                .select(UserAcProblem::getPid, UserAcProblem::getSubmitId)
                .eq(UserAcProblem::getUid, uuid)
                .orderByAsc(UserAcProblem::getSubmitId)
                .list();
    }
}
