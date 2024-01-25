package love.pangteen.problem.controller;

import love.pangteen.api.annotations.IgnoreLogin;
import love.pangteen.problem.constants.OJConstants;
import love.pangteen.problem.pojo.entity.Tag;
import love.pangteen.problem.pojo.vo.ProblemTagVO;
import love.pangteen.problem.service.ClassificationService;
import love.pangteen.problem.service.ProblemTagService;
import love.pangteen.problem.service.TagService;
import love.pangteen.result.CommonResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/24 22:54
 **/
@RestController
@RequestMapping("/tag")
public class ProblemTagController {

    @Resource
    private TagService tagService;

    @Resource
    private ProblemTagService problemTagService;

    @Resource
    private ClassificationService classificationService;

    @GetMapping("/get-all-problem-tags")
    @IgnoreLogin
    public CommonResult<List<Tag>> getAllProblemTagsList(@RequestParam(value = "oj", defaultValue = OJConstants.DEFAULT_TAG_SOURCE) String oj) {
        return CommonResult.success(tagService.getAllProblemTagsList(oj));
    }

    @GetMapping("/get-problem-tags-and-classification")
    @IgnoreLogin
    public CommonResult<List<ProblemTagVO>> getProblemTagsAndClassification(@RequestParam(value = "oj", defaultValue = OJConstants.DEFAULT_TAG_SOURCE) String oj) {
        return CommonResult.success(classificationService.getProblemTagsAndClassification(oj));
    }

    @GetMapping("/get-problem-tags")
    @IgnoreLogin
    public CommonResult<Collection<Tag>> getProblemTags(Long pid) {
        return CommonResult.success(problemTagService.getProblemTags(pid));
    }

}
