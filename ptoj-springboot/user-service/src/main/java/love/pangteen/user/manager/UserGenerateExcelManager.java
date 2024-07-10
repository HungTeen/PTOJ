package love.pangteen.user.manager;

import com.alibaba.excel.EasyExcel;
import love.pangteen.user.pojo.vo.ExcelUserVO;
import love.pangteen.utils.RedisUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/21 19:15
 **/
@Component
public class UserGenerateExcelManager {

    @Resource
    private RedisUtils redisUtils;

    public void setGenerateMap(String key, Map<String, Object> userInfoMap){
        redisUtils.hmPutAll(key, userInfoMap, 1800); // 存储半小时
    }

    public void generateUserExcel(String key, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码
        String fileName = URLEncoder.encode(key, StandardCharsets.UTF_8);
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        response.setHeader("Content-Type", "application/xlsx");
        EasyExcel.write(response.getOutputStream(), ExcelUserVO.class)
                .sheet("用户数据")
                .doWrite(getGenerateUsers(key));
    }

    private List<ExcelUserVO> getGenerateUsers(String key) {
        List<ExcelUserVO> result = new LinkedList<>();
        Map<Object, Object> userInfo = redisUtils.hmGet(key);
        for (Object hashKey : userInfo.keySet()) {
            String username = (String) hashKey;
            String password = (String) userInfo.get(hashKey);
            result.add(new ExcelUserVO().setUsername(username).setPassword(password));
        }
        return result;
    }
}
