package love.pangteen.training;

import cn.dev33.satoken.stp.StpUtil;
import love.pangteen.api.enums.TrainingAuth;
import love.pangteen.api.utils.RoleUtils;
import love.pangteen.exception.StatusAccessDeniedException;
import love.pangteen.exception.StatusForbiddenException;
import love.pangteen.pojo.AccountProfile;
import love.pangteen.training.pojo.entity.Training;
import love.pangteen.training.pojo.entity.TrainingRegister;
import love.pangteen.training.service.TrainingRegisterService;
import love.pangteen.utils.AccountUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/2/27 9:45
 **/
@Component
public class TrainingValidateUtils {

    @Resource
    private TrainingRegisterService trainingRegisterService;

    public void validateTrainingAuth(Training training) throws StatusAccessDeniedException, StatusForbiddenException {
        validateTrainingAuth(training, AccountUtils.getProfile());
    }

    public void validateTrainingAuth(Training training, AccountProfile profile) throws StatusAccessDeniedException, StatusForbiddenException {
        if (training.getIsGroup()) {
//            if (!groupValidator.isGroupMember(profile.getUuid(), training.getGid()) && !isRoot) {
//                throw new StatusForbiddenException("对不起，您并非该团队内的成员，无权操作！");
//            }
        }

        if (TrainingAuth.isPrivate(training.getAuth())) {

            if (profile == null) {
                throw new StatusAccessDeniedException("该训练属于私有题单，请先登录以校验权限！");
            }

            boolean isAuthor = training.getAuthor().equals(profile.getUsername()); // 是否为该私有训练的创建者

            if (StpUtil.hasRole(RoleUtils.getRoot()) || isAuthor
//                    || (training.getIsGroup() && groupValidator.isGroupRoot(profile.getUid(), training.getGid()))
            ) {
                return;
            }

            // 如果三者都不是，需要做注册权限校验
            checkTrainingRegister(training.getId(), profile.getUuid());
        }
    }

    private void checkTrainingRegister(Long tid, String uid) throws StatusAccessDeniedException, StatusForbiddenException {
        TrainingRegister trainingRegister = trainingRegisterService.lambdaQuery()
                .eq(TrainingRegister::getTid, tid)
                .eq(TrainingRegister::getUid, uid)
                .one();

        if (trainingRegister == null) {
            throw new StatusAccessDeniedException("该训练属于私有，请先使用专属密码注册！");
        }

        if (!trainingRegister.getStatus()) {
            throw new StatusForbiddenException("错误：你已被禁止参加该训练！");
        }
    }
}
