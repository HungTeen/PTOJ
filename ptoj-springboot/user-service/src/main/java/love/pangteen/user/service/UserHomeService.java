package love.pangteen.user.service;

import love.pangteen.user.pojo.dto.EditUserInfoDTO;
import love.pangteen.user.pojo.vo.UserCalendarHeatmapVO;
import love.pangteen.user.pojo.vo.UserHomeVO;
import love.pangteen.user.pojo.vo.UserInfoVO;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/7/15 15:09
 **/
public interface UserHomeService {

    UserInfoVO changeUserInfo(EditUserInfoDTO editUserInfoDTO);

    UserHomeVO getUserHomeInfo(String uid, String username);

    UserCalendarHeatmapVO getUserCalendarHeatmap(String uid, String username);

}
