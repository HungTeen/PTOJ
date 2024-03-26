package love.pangteen.judge.pojo.vo;

import lombok.Data;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/2/1 16:22
 **/
@Data
public class SystemConfigVO {

    private String name;

    private Integer cpuCores;

    private String percentCpuLoad;

    private String percentMemoryLoad;

}
