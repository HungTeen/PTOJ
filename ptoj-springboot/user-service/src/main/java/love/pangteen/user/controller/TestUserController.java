package love.pangteen.user.controller;

import love.pangteen.result.CommonResult;
import love.pangteen.user.pojo.vo.ACMRankVO;
import love.pangteen.user.service.HomeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/6/19 20:59
 **/
@RestController
@RequestMapping("/test/user")
public class TestUserController {

    @Resource
    private HomeService homeService;

    @GetMapping("/get-rank")
    public CommonResult<List<ACMRankVO>> getRecentSevenACRank(@RequestParam Boolean cached) {
        return CommonResult.success(homeService.getRecentSevenACRank(cached));
    }

}
