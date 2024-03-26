package love.pangteen.judge.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.system.oshi.OshiUtil;
import love.pangteen.judge.config.properties.OJProperties;
import love.pangteen.judge.pojo.vo.JudgeServerInfoVO;
import love.pangteen.judge.pojo.vo.SystemConfigVO;
import love.pangteen.judge.sandbox.SandboxManager;
import love.pangteen.judge.service.OJService;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/25 15:19
 **/
@Service
@RefreshScope
public class OJServiceImpl implements OJService {

    @Resource
    private OJProperties ojProperties;

    @Override
    public JudgeServerInfoVO getJudgeServerInfo() {
        JudgeServerInfoVO.JudgeServerInfoVOBuilder builder = JudgeServerInfoVO.builder();
        int maxTaskNum = 2;
        boolean isOpenRemoteJudge = false;
        int RemoteJudgeMaxTaskNum = 1;
        builder.version("20240202")
                .currentTime(new Date())
                .judgeServerName(ojProperties.getName())
                .cpu(Runtime.getRuntime().availableProcessors())
                .languages(Arrays.asList("G++ 9.4.0", "GCC 9.4.0", "Python 3.7.5",
                        "Python 2.7.17", "OpenJDK 1.8", "Golang 1.19", "C# Mono 4.6.2",
                        "PHP 7.2.24", "JavaScript Node 14.19.0", "JavaScript V8 8.4.109",
                        "PyPy 2.7.18 (7.3.9)", "PyPy 3.9.17 (7.3.12)", "Ruby 2.5.1", "Rust 1.65.0"))
                .maxTaskNum(maxTaskNum == -1 ? Runtime.getRuntime().availableProcessors() + 1 : maxTaskNum);
        if (isOpenRemoteJudge) {
            builder.isOpenRemoteJudge(true);
            builder.remoteJudgeMaxTaskNum(RemoteJudgeMaxTaskNum == -1 ? Runtime.getRuntime().availableProcessors() * 2 + 1 : RemoteJudgeMaxTaskNum);
        }

        try {
            builder.sandBoxMsg(JSONUtil.parseObj(SandboxManager.version()));
        } catch (Exception e) {
            builder.sandBoxMsg(MapUtil.builder().put("error", e.getMessage()).map());
        }

        return builder.build();
    }

    @Override
    public SystemConfigVO getSystemConfig() {
        SystemConfigVO systemConfigVO = new SystemConfigVO();
        systemConfigVO.setName(ojProperties.getName());
        systemConfigVO.setCpuCores(Runtime.getRuntime().availableProcessors()); // cpu核数

        double cpuLoad = 100 - OshiUtil.getCpuInfo().getFree();
        systemConfigVO.setPercentCpuLoad(String.format("%.2f", cpuLoad) + "%"); // cpu使用率

        double totalVirtualMemory = OshiUtil.getMemory().getTotal(); // 总内存
        double freePhysicalMemorySize = OshiUtil.getMemory().getAvailable(); // 空闲内存
        double value = freePhysicalMemorySize / totalVirtualMemory;
        systemConfigVO.setPercentMemoryLoad(String.format("%.2f", (1 - value) * 100) + "%"); // 内存使用率
        return systemConfigVO;
    }

}
