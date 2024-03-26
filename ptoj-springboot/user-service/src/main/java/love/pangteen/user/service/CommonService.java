package love.pangteen.user.service;

import cn.hutool.json.JSONObject;
import love.pangteen.user.pojo.vo.DashboardVO;

import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/3/25 22:09
 **/
public interface CommonService {


    DashboardVO getDashboardInfo();

    List<JSONObject> getJudgeServiceInfo();
}
