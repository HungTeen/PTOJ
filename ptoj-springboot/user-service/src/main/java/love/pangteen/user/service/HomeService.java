package love.pangteen.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import love.pangteen.user.pojo.vo.ACMRankVO;
import love.pangteen.user.pojo.vo.AnnouncementVO;
import love.pangteen.user.pojo.vo.SubmissionStatisticsVO;

import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/3/26 22:46
 **/
public interface HomeService {

    List<ACMRankVO> getRecentSevenACRank();

    List<ACMRankVO> getRecentSevenACRank(Boolean cached);

    SubmissionStatisticsVO getLastWeekSubmissionStatistics(Boolean forceRefresh);

    IPage<AnnouncementVO> getAnnouncements(Integer limit, Integer currentPage);
}
