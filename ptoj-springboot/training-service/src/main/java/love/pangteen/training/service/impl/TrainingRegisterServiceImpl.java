package love.pangteen.training.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import love.pangteen.api.pojo.vo.AccessVO;
import love.pangteen.exception.StatusFailException;
import love.pangteen.pojo.AccountProfile;
import love.pangteen.training.mapper.TrainingMapper;
import love.pangteen.training.mapper.TrainingRegisterMapper;
import love.pangteen.training.pojo.dto.RegisterTrainingDTO;
import love.pangteen.training.pojo.entity.Training;
import love.pangteen.training.pojo.entity.TrainingRegister;
import love.pangteen.training.service.TrainingRegisterService;
import love.pangteen.utils.AccountUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/2/26 10:36
 **/
@Service
public class TrainingRegisterServiceImpl extends ServiceImpl<TrainingRegisterMapper, TrainingRegister> implements TrainingRegisterService {

    @Resource
    private TrainingMapper trainingMapper;

    @Override
    public void registerTraining(RegisterTrainingDTO registerDTO) {
        Training training = trainingMapper.selectById(registerDTO.getTid());
        if (training == null || !training.getStatus()) {
            throw new StatusFailException("对不起，该训练不存在或不允许显示!");
        }
        if (!training.getPrivatePwd().equals(registerDTO.getPassword())) { // 密码不对
            throw new StatusFailException("训练密码错误，请重新输入！");
        }
        AccountProfile profile = AccountUtils.getProfile();
        if(lambdaQuery()
                .eq(TrainingRegister::getTid, registerDTO.getTid())
                .eq(TrainingRegister::getUid, profile.getUuid())
                .oneOpt().isPresent()){
            throw new StatusFailException("您已注册过该训练，请勿重复注册！");
        }
        if (!save(new TrainingRegister().setTid(registerDTO.getTid()).setUid(profile.getUuid()))) {
            throw new StatusFailException("校验训练密码失败，请稍后再试");
        } else {
            // TODO adminTrainingRecordManager.syncUserSubmissionToRecordByTid(tid, userRolesVo.getUid());
        }
    }

    @Override
    public void removeByTid(Long id) {
        lambdaUpdate()
                .eq(TrainingRegister::getTid, id)
                .remove();
    }

    @Override
    public AccessVO getTrainingAccess(Long tid) {
        AccountProfile profile = AccountUtils.getProfile();
        AccessVO accessVO = new AccessVO(false);
        if(lambdaQuery()
                .eq(TrainingRegister::getTid, tid)
                .eq(TrainingRegister::getUid, profile.getUuid())
                .eq(TrainingRegister::getStatus, true)
                .oneOpt().isPresent()){
            Training training = trainingMapper.selectById(tid);
            if(training != null && training.getStatus()){
                accessVO.setAccess(true);
            } else {
                throw new StatusFailException("对不起，该训练不存在!");
            }
        }
        return accessVO;
    }
}
