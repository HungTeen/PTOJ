package love.pangteen.user.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import love.pangteen.user.manager.UserAcceptManager;
import love.pangteen.user.pojo.vo.ACMRankVO;
import love.pangteen.user.pojo.vo.AnnouncementVO;
import love.pangteen.user.pojo.vo.SubmissionStatisticsVO;
import love.pangteen.user.service.HomeService;
import love.pangteen.user.service.UserInfoService;
import love.pangteen.utils.RedisUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/3/26 22:47
 **/
@Slf4j
@Service
public class HomeServiceImpl implements HomeService {

    @Resource
    private UserAcceptManager userAcceptManager;

    @Resource
    private UserInfoService userInfoService;

    @Override
    public List<ACMRankVO> getRecentSevenACRank() {
        List<ZSetOperations.TypedTuple<String>> topUsers = userAcceptManager.getTopUsers();
        List<ACMRankVO> acmRankVOS = topUsers.stream().map(tuple -> {
            ACMRankVO acmRankVO = new ACMRankVO();
            BeanUtils.copyProperties(userInfoService.getById(tuple.getValue()), acmRankVO);
            acmRankVO.setUid(tuple.getValue());
            acmRankVO.setAc(tuple.getScore().intValue());
            return acmRankVO;
        }).collect(Collectors.toList());
        return acmRankVOS;
    }

    @Override
    public SubmissionStatisticsVO getLastWeekSubmissionStatistics(Boolean forceRefresh) {
        return new SubmissionStatisticsVO();
    }

    @Override
    public IPage<AnnouncementVO> getAnnouncements(Integer limit, Integer currentPage) {
        Page<AnnouncementVO> page = new Page<>(limit, currentPage);
        page.setRecords(List.of(
                AnnouncementVO.builder()
                        .title("Welcome to PTOJ")
                        .content("Guys, welcome to PTOJ, this is a test announcement.")
                        .build()
        ));
        return page;
    }

}
