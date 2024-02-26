package love.pangteen.training.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import love.pangteen.api.annotations.IgnoreLogin;
import love.pangteen.api.pojo.vo.AccessVO;
import love.pangteen.api.pojo.vo.ProblemVO;
import love.pangteen.result.CommonResult;
import love.pangteen.training.pojo.dto.RegisterTrainingDTO;
import love.pangteen.training.pojo.vo.TrainingVO;
import love.pangteen.training.service.TrainingProblemService;
import love.pangteen.training.service.TrainingRegisterService;
import love.pangteen.training.service.TrainingService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/2/26 15:06
 **/
@RestController
@RequestMapping("/training")
public class TrainingController {

    @Resource
    private TrainingService trainingService;

    @Resource
    private TrainingRegisterService trainingRegisterService;

    @Resource
    private TrainingProblemService trainingProblemService;

    /**
     * 获取训练题单列表，可根据关键词、类别、权限、类型过滤。
     */
    @GetMapping("/get-training-list")
    @IgnoreLogin
    public CommonResult<IPage<TrainingVO>> getTrainingList(@RequestParam(value = "limit", defaultValue = "10") Integer limit,
                                                           @RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
                                                           @RequestParam(value = "keyword", required = false) String keyword,
                                                           @RequestParam(value = "categoryId", required = false) Long categoryId,
                                                           @RequestParam(value = "auth", required = false) String auth) {

        return CommonResult.success(trainingProblemService.getTrainingDetailList(limit, currentPage, keyword, categoryId, auth));
    }


    /**
     * 根据tid获取指定训练详情。
     */
    @GetMapping("/get-training-detail")
    public CommonResult<TrainingVO> getTraining(@RequestParam(value = "tid") Long tid) {
        return CommonResult.success(trainingProblemService.getTrainingDetail(tid));
    }

    /**
     * 根据tid获取指定训练的题单题目列表。
     */
    @GetMapping("/get-training-problem-list")
    public CommonResult<List<ProblemVO>> getTrainingProblemList(@RequestParam(value = "tid") Long tid) {
        return CommonResult.success(trainingProblemService.getTrainingProblemList(tid));
    }

    /**
     * 注册校验私有权限的训练。
     */
    @PostMapping("/register-training")
    public CommonResult<Void> toRegisterTraining(@Validated @RequestBody RegisterTrainingDTO registerTrainingDto) {
        trainingRegisterService.registerTraining(registerTrainingDto);
        return CommonResult.success();
    }


    /**
     * 私有权限的训练需要获取当前用户是否有进入训练的权限。
     */
    @GetMapping("/get-training-access")
    public CommonResult<AccessVO> getTrainingAccess(@RequestParam(value = "tid") Long tid) {
        return CommonResult.success(trainingRegisterService.getTrainingAccess(tid));
    }


//    /**
//     * 获取训练的排行榜分页。
//     */
//    @GetMapping("/get-training-rank")
//    public CommonResult<IPage<TrainingRankVO>> getTrainingRank(@RequestParam(value = "tid", required = true) Long tid,
//                                                               @RequestParam(value = "limit", required = false) Integer limit,
//                                                               @RequestParam(value = "currentPage", required = false) Integer currentPage,
//                                                               @RequestParam(value = "keyword", required = false) String keyword) {
//        return CommonResult.success(trainingService.getTrainingRank(tid, limit, currentPage, keyword));
//    }

}
