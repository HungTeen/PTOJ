package love.pangteen.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import love.pangteen.result.CommonResult;
import love.pangteen.user.pojo.vo.ACMRankVO;
import love.pangteen.user.pojo.vo.AnnouncementVO;
import love.pangteen.user.pojo.vo.SubmissionStatisticsVO;
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
 * @create: 2024/3/26 22:31
 **/
@RestController
@RequestMapping("/home")
public class HomeController {

    @Resource
    private HomeService homeService;

    @GetMapping("/get-common-announcement")
    public CommonResult<IPage<AnnouncementVO>> getCommonAnnouncement(@RequestParam(value = "limit", required = false) Integer limit,
                                                                     @RequestParam(value = "currentPage", required = false) Integer currentPage) {
        return CommonResult.success(homeService.getAnnouncements(limit, currentPage));
    }

    @GetMapping("/get-recent-seven-ac-rank")
    public CommonResult<List<ACMRankVO>> getRecentSevenACRank() {
        return CommonResult.success(homeService.getRecentSevenACRank());
    }

    @GetMapping("/get-last-week-submission-statistics")
    public CommonResult<SubmissionStatisticsVO> getLastWeekSubmissionStatistics(
            @RequestParam(value = "forceRefresh", defaultValue = "false") Boolean forceRefresh) {
        return CommonResult.success(homeService.getLastWeekSubmissionStatistics(forceRefresh));
    }

}
