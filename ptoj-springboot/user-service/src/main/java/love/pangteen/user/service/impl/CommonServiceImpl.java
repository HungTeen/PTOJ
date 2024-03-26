package love.pangteen.user.service.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.cloud.nacos.discovery.NacosDiscoveryClient;
import lombok.extern.slf4j.Slf4j;
import love.pangteen.api.constant.ServiceConstants;
import love.pangteen.api.service.IDubboJudgeService;
import love.pangteen.user.pojo.vo.DashboardVO;
import love.pangteen.user.service.CommonService;
import love.pangteen.user.service.UserInfoService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.rpc.RpcException;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/3/25 22:10
 **/
@Slf4j
@Service
public class CommonServiceImpl implements CommonService {

    @Resource
    private UserInfoService userInfoService;

    @DubboReference
    private IDubboJudgeService dubboJudgeService;

    @Resource
    private DiscoveryClient discoveryClient;

    @Resource
    private RestTemplate restTemplate;

    @Override
    public DashboardVO getDashboardInfo() {
        DashboardVO.DashboardVOBuilder builder = DashboardVO.builder();
        builder.userNum(userInfoService.getTotalUserCount());
        try {
            builder.todayJudgeNum(dubboJudgeService.getTodayJudgeCount());
        } catch (RpcException e){
            log.error("获取今日评测数失败：服务不可用");
        }
        return builder.build();
    }

    @Override
    public List<JSONObject> getJudgeServiceInfo() {
        return discoveryClient.getInstances(ServiceConstants.JUDGE_SERVICE).stream().parallel().map(instance -> {
            String result = restTemplate.getForObject(instance.getUri() + "/judge/get-sys-config", String.class);
            JSONObject jsonObject = JSONUtil.parseObj(result, false);
            jsonObject.set("service", instance);
            return jsonObject;
        }).collect(Collectors.toList());
    }

}
