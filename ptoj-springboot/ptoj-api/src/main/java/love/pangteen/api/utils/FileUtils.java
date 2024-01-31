package love.pangteen.api.utils;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.util.regex.Pattern;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/31 10:52
 **/
@Slf4j
public class FileUtils {

    private final static Pattern EOL_PATTERN = Pattern.compile("[^\\S\\n]+(?=\\n)");

    public static Iterable<Object> loadYml(String fileName) {
        try {
            Yaml yaml = new Yaml();
            String ymlContent = ResourceUtil.readUtf8Str(fileName);
            return yaml.loadAll(ymlContent);
        } catch (Exception e) {
            log.error(String.format("load %s yaml error:", fileName), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 去除每行末尾的空白符。
     */
    public static String rtrim(String value) {
        if (value == null) return null;
        return FileUtils.EOL_PATTERN.matcher(StrUtil.trimEnd(value)).replaceAll("");
    }

}
