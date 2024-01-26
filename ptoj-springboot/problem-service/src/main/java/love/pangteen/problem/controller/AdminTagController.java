package love.pangteen.problem.controller;

import love.pangteen.api.constant.OJConstant;
import love.pangteen.problem.pojo.entity.Tag;
import love.pangteen.problem.pojo.entity.TagClassification;
import love.pangteen.problem.service.ClassificationService;
import love.pangteen.problem.service.TagService;
import love.pangteen.result.CommonResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/24 11:02
 **/
@RestController
@RequestMapping("/admin/tag")
public class AdminTagController {

    @Resource
    private TagService tagService;

    @Resource
    private ClassificationService classificationService;

    @PostMapping("")
    public CommonResult<Tag> addTag(@RequestBody Tag tag) {
        return CommonResult.success(tagService.addTag(tag));
    }

    @PutMapping("")
    public CommonResult<Void> updateTag(@RequestBody Tag tag) {
        tagService.updateById(tag);
        return CommonResult.success();
    }

    @DeleteMapping("")
    public CommonResult<Void> deleteTag(@RequestParam("tid") Long tid) {
        tagService.removeById(tid);
        return CommonResult.success();
    }

    @GetMapping("/classification")
    public CommonResult<List<TagClassification>> getTagClassification(@RequestParam(value = "oj", defaultValue = OJConstant.DEFAULT_TAG_SOURCE) String oj) {
        return CommonResult.success(classificationService.getTagClassification(oj));
    }

    @PostMapping("/classification")
    public CommonResult<TagClassification> addTagClassification(@RequestBody TagClassification tagClassification) {
        return CommonResult.success(classificationService.addTagClassification(tagClassification));
    }

    @PutMapping("/classification")
    public CommonResult<Void> updateTagClassification(@RequestBody TagClassification tagClassification) {
        classificationService.updateById(tagClassification);
        return CommonResult.success();
    }

    @DeleteMapping("/classification")
    public CommonResult<Void> deleteTagClassification(@RequestParam("tcid") Long tcid) {
        classificationService.removeById(tcid);
        return CommonResult.success();
    }
}
