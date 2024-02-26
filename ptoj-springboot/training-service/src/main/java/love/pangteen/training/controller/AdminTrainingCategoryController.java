package love.pangteen.training.controller;

import love.pangteen.result.CommonResult;
import love.pangteen.training.pojo.entity.TrainingCategory;
import love.pangteen.training.service.TrainingCategoryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/2/26 10:27
 **/
@RestController
@RequestMapping("/admin/training/category")
public class AdminTrainingCategoryController {

    @Resource
    private TrainingCategoryService trainingCategoryService;

    @PostMapping("")
    public CommonResult<TrainingCategory> addTrainingCategory(@RequestBody TrainingCategory trainingCategory) {
        return CommonResult.success(trainingCategoryService.addTrainingCategory(trainingCategory));
    }

    @PutMapping("")
    public CommonResult<Void> updateTrainingCategory(@RequestBody TrainingCategory trainingCategory) {
        trainingCategoryService.updateTrainingCategory(trainingCategory);
        return CommonResult.success();
    }

    @DeleteMapping("")
    public CommonResult<Void> deleteTrainingCategory(@RequestParam("cid") Long cid) {
        trainingCategoryService.deleteTrainingCategory(cid);
        return CommonResult.success();
    }

}
