package love.pangteen.training.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import love.pangteen.api.enums.TrainingAuth;
import love.pangteen.api.utils.RoleUtils;
import love.pangteen.exception.StatusFailException;
import love.pangteen.training.mapper.TrainingMapper;
import love.pangteen.training.pojo.dto.TrainingDTO;
import love.pangteen.training.pojo.entity.Training;
import love.pangteen.training.pojo.entity.TrainingCategory;
import love.pangteen.training.service.MappingTrainingCategoryService;
import love.pangteen.training.service.TrainingRegisterService;
import love.pangteen.training.service.TrainingService;
import love.pangteen.utils.AccountUtils;
import love.pangteen.utils.ValidateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Objects;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/2/26 10:36
 **/
@Service
public class TrainingServiceImpl extends ServiceImpl<TrainingMapper, Training> implements TrainingService {

    @Resource
    private MappingTrainingCategoryService mappingTrainingCategoryService;

    @Resource
    private TrainingRegisterService trainingRegisterService;

    @Override
    public IPage<Training> getTrainingList(Integer limit, Integer currentPage, String keyword) {
        return lambdaQuery()
                .select(Training.class, info -> !info.getColumn().equals("private_pwd"))
                .and(StrUtil.isNotEmpty(keyword), wrapper -> {
                    wrapper.like(Training::getTitle, keyword).or().like(Training::getAuthor, keyword).or().like(Training::getRank, keyword);
                })
                .eq(Training::getIsGroup, false)
                .orderByAsc(Training::getRank)
                .page(new Page<>(currentPage, limit));
    }

    @Override
    public TrainingDTO getTraining(Long tid) {
        Training training = getById(tid);
        if (training == null) { // 查询不存在
            throw new StatusFailException("查询失败：该训练不存在,请检查参数tid是否准确！");
        }

        ValidateUtils.validateAuthorOrRoles(AccountUtils.getProfile(), training.getAuthor(), "对不起，你无权限操作！", RoleUtils.getRoot());

        TrainingCategory trainingCategory = mappingTrainingCategoryService.getTrainingCategory(tid);

        return new TrainingDTO().setTraining(training).setTrainingCategory(trainingCategory);
    }

    @Transactional
    @Override
    public void deleteTraining(Long tid) {
        boolean isOk = removeById(tid);
        /*
        Training的id为其他表的外键的表中的对应数据都会被一起删除！
         */
        if (!isOk) {
            throw new StatusFailException("删除失败！");
        } else {
            mappingTrainingCategoryService.removeByTid(tid);
        }
    }

    @Transactional
    @Override
    public void addTraining(TrainingDTO trainingDto) {
        Training training = trainingDto.getTraining();
        save(training);
        if (!mappingTrainingCategoryService.saveTrainingCategory(training.getId(), trainingDto.getTrainingCategory())) {
            throw new StatusFailException("添加失败！");
        }
    }

    @Transactional
    @Override
    public void updateTraining(TrainingDTO trainingDto) {
        Training training = trainingDto.getTraining();
        ValidateUtils.validateAuthorOrRoles(AccountUtils.getProfile(), training.getAuthor(), "对不起，你无权限操作！", RoleUtils.getRoot());

        Training oldTraining = getById(training.getId());
        updateById(training);

        // 私有训练若修改了密码，则需要清空之前注册训练的记录。
        if (TrainingAuth.isPrivate(training.getAuth()) && !Objects.equals(training.getPrivatePwd(), oldTraining.getPrivatePwd())) {
            trainingRegisterService.removeByTid(training.getId());
        }

        if (!mappingTrainingCategoryService.updateTrainingCategory(training, trainingDto.getTrainingCategory())) {
            throw new StatusFailException("修改失败！");
        }
    }

    @Override
    public void changeTrainingStatus(Long tid, String author, Boolean status) {
        ValidateUtils.validateAuthorOrRoles(AccountUtils.getProfile(), author, "对不起，你无权限操作！", RoleUtils.getRoot());
        if (!lambdaUpdate()
                .eq(Training::getId, tid)
                .set(Training::getStatus, status)
                .update()){
            throw new StatusFailException("修改失败");
        }
    }

    @Override
    public void modified(Long tid) {
        lambdaUpdate().eq(Training::getId, tid).set(Training::getGmtModified, new Date()).update();
    }

}
