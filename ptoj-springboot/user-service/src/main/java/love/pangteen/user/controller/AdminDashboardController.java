package love.pangteen.user.controller;

import cn.hutool.json.JSONObject;
import love.pangteen.result.CommonResult;
import love.pangteen.user.pojo.vo.DashboardVO;
import love.pangteen.user.service.CommonService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/3/25 21:57
 **/
@RestController
@RequestMapping("/admin/dashboard")
public class AdminDashboardController {

    @Resource
    private CommonService commonService;

    @GetMapping("/get-dashboard-info")
    public CommonResult<DashboardVO> getDashboardInfo(){
        return CommonResult.success(commonService.getDashboardInfo());
    }

    @RequestMapping("/get-judge-service-info")
    public CommonResult<List<JSONObject>> getJudgeServiceInfo() {
        return CommonResult.success(commonService.getJudgeServiceInfo());
    }

}
