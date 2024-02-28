package love.pangteen.api.utils;

import cn.hutool.core.util.StrUtil;

import java.util.Calendar;
import java.util.Date;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/24 9:47
 **/
public class Utils {

    public static String mergeNonEmptyStrings(String... strings) {
        StringBuilder sb = new StringBuilder();
        for (String str : strings) {
            if (!StrUtil.isEmpty(str)) {
                sb.append(str, 0, Math.min(1024 * 1024, str.length())).append("\n");
            }
        }
        return sb.toString();
    }

    public static Date getYearAgo(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.YEAR, -year);
        return calendar.getTime();
    }
}
