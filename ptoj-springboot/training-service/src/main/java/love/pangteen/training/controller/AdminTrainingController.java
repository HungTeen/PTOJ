package love.pangteen.training.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import love.pangteen.result.CommonResult;
import love.pangteen.training.pojo.dto.TrainingDTO;
import love.pangteen.training.pojo.dto.TrainingProblemDTO;
import love.pangteen.training.pojo.entity.Training;
import love.pangteen.training.pojo.entity.TrainingProblem;
import love.pangteen.training.pojo.vo.TrainingProblemListVO;
import love.pangteen.training.service.TrainingProblemService;
import love.pangteen.training.service.TrainingService;
import org.hibernate.validator.constraints.Range;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/2/26 10:27
 **/
@RestController
@RequestMapping("/admin/training")
public class AdminTrainingController {

    @Resource
    private TrainingService trainingService;

    @Resource
    private TrainingProblemService trainingProblemService;

    @GetMapping("/get-training-list")
    @Validated
    public CommonResult<IPage<Training>> getTrainingList(@Range(min = 1) @RequestParam(value = "limit", defaultValue = "10") Integer limit,
                                                         @Range(min = 1) @RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
                                                         @RequestParam(value = "keyword", required = false) String keyword) {
        return CommonResult.success(trainingService.getTrainingList(limit, currentPage, keyword));
    }

    @GetMapping("")
    public CommonResult<TrainingDTO> getTraining(@RequestParam("tid") Long tid) {
        return CommonResult.success(trainingService.getTraining(tid));
    }

    @DeleteMapping("")
    public CommonResult<Void> deleteTraining(@RequestParam("tid") Long tid) {
        trainingService.deleteTraining(tid);
        return CommonResult.success();
    }

    @PostMapping("")
    public CommonResult<Void> addTraining(@Validated @RequestBody TrainingDTO trainingDto) {
        trainingService.addTraining(trainingDto);
        return CommonResult.success();
    }

    @PutMapping("")
    public CommonResult<Void> updateTraining(@Validated @RequestBody TrainingDTO trainingDto) {
        trainingService.updateTraining(trainingDto);
        return CommonResult.success();
    }


    @PutMapping("/change-training-status")
    public CommonResult<Void> changeTrainingStatus(@RequestParam(value = "tid", required = true) Long tid,
                                                   @RequestParam(value = "author", required = true) String author,
                                                   @RequestParam(value = "status", required = true) Boolean status) {
        trainingService.changeTrainingStatus(tid, author, status);
        return CommonResult.success();
    }

    @GetMapping("/get-problem-list")
    @Validated
    public CommonResult<TrainingProblemListVO> getProblemList(@RequestParam(value = "limit", defaultValue = "10") Integer limit,
                                                              @RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
                                                              @RequestParam(value = "keyword", required = false) String keyword,
                                                              @RequestParam(value = "queryExisted", defaultValue = "false") Boolean queryExisted,
                                                              @RequestParam(value = "tid", required = true) Long tid) {
        return CommonResult.success(trainingProblemService.getProblemList(limit, currentPage, keyword, queryExisted, tid));
    }


    @PutMapping("/problem")
    public CommonResult<Void> updateProblem(@RequestBody TrainingProblem trainingProblem) {
        trainingProblemService.updateProblem(trainingProblem);
        return CommonResult.success();
    }

    @DeleteMapping("/problem")
    public CommonResult<Void> deleteProblem(@RequestParam("pid") Long pid,
                                            @RequestParam(value = "tid", required = false) Long tid) {
        trainingProblemService.deleteProblem(pid, tid);
        return CommonResult.success();
    }

    @PostMapping("/add-problem-from-public")
    public CommonResult<Void> addProblemFromPublic(@RequestBody TrainingProblemDTO trainingProblemDto) {
        trainingProblemService.addProblemFromPublic(trainingProblemDto);
        return CommonResult.success();
    }

//    @GetMapping("/import-remote-oj-problem")
//    @Transactional(rollbackFor = Exception.class)
//    public CommonResult<Void> importTrainingRemoteOJProblem(@RequestParam("name") String name,
//                                                            @RequestParam("problemId") String problemId,
//                                                            @RequestParam("tid") Long tid) {
//        return adminTrainingProblemService.importTrainingRemoteOJProblem(name, problemId, tid);
//    }

}
