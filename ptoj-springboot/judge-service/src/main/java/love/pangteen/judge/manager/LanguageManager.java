package love.pangteen.judge.manager;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import love.pangteen.api.utils.FileUtils;
import love.pangteen.judge.pojo.entity.LanguageConfig;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/30 15:56
 **/
@Slf4j
public class LanguageManager {

    public static final String LANGUAGE_CONFIG_FILENAME = "language.yml";
    private static final List<String> DEFAULT_ENV = Arrays.asList(
            "PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin",
            "LANG=en_US.UTF-8", "LC_ALL=en_US.UTF-8", "LANGUAGE=en_US:en", "HOME=/w"
    );

    private static final List<String> PYTHON3_ENV = Arrays.asList(
            "LANG=en_US.UTF-8", "LANGUAGE=en_US:en", "LC_ALL=en_US.UTF-8", "PYTHONIOENCODING=utf-8"
    );

    private static final List<String> GO_COMPILE_ENV = Arrays.asList(
            "GOCACHE=/w", "GOPATH=/w/go", "PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin",
            "LANG=en_US.UTF-8", "LANGUAGE=en_US:en", "LC_ALL=en_US.UTF-8"
    );

    private static final List<String> GO_RUN_ENV = Arrays.asList(
            "GOCACHE=off", "GODEBUG=madvdontneed=1", "PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin",
            "LANG=en_US.UTF-8", "LANGUAGE=en_US:en", "LC_ALL=en_US.UTF-8");

    private static final HashMap<String, LanguageConfig> CONFIG_MAP = new HashMap<>();

    static {
        Iterable<Object> languageConfigIter = FileUtils.loadYml(LANGUAGE_CONFIG_FILENAME);
        for (Object configObj : languageConfigIter) {
            JSONObject configJson = JSONUtil.parseObj(configObj);
            LanguageConfig languageConfig = buildLanguageConfig(configJson);
            CONFIG_MAP.put(languageConfig.getLanguage(), languageConfig);
        }
        log.info("load language config: {}", CONFIG_MAP);
    }

    public static LanguageConfig getLanguageConfigByName(String langName) {
        return CONFIG_MAP.get(langName);
    }

    /**
     * 读取配置文件。
     */
    private static LanguageConfig buildLanguageConfig(JSONObject configJson) {
        LanguageConfig languageConfig = new LanguageConfig();
        languageConfig.setLanguage(configJson.getStr("language"));
        languageConfig.setSrcName(configJson.getStr("src_path"));
        languageConfig.setExeName(configJson.getStr("exe_path"));

        JSONObject compileJson = configJson.getJSONObject("compile");
        if (compileJson != null) {
            String command = compileJson.getStr("command");
            command = command.replace("{src_path}", languageConfig.getSrcName())
                    .replace("{exe_path}", languageConfig.getExeName());
            languageConfig.setCompileCommand(command);
            String env = compileJson.getStr("env");
            env = env.toLowerCase();
            switch (env) {
                case "python3":
                    languageConfig.setCompileEnvs(PYTHON3_ENV);
                    break;
                case "golang_compile":
                    languageConfig.setCompileEnvs(GO_COMPILE_ENV);
                    break;
                default:
                    languageConfig.setCompileEnvs(DEFAULT_ENV);
            }
            languageConfig.setMaxCpuTime(parseTimeStr(compileJson.getStr("maxCpuTime")));
            languageConfig.setMaxRealTime(parseTimeStr(compileJson.getStr("maxRealTime")));
            languageConfig.setMaxMemory(parseMemoryStr(compileJson.getStr("maxMemory")));
        }

        JSONObject runJson = configJson.getJSONObject("run");
        if (runJson != null) {
            String command = runJson.getStr("command");
            command = command.replace("{exe_path}", languageConfig.getExeName());
            languageConfig.setRunCommand(command);
            String env = runJson.getStr("env");
            env = env.toLowerCase();
            switch (env) {
                case "python3":
                    languageConfig.setRunEnvs(PYTHON3_ENV);
                    break;
                case "golang_run":
                    languageConfig.setRunEnvs(GO_RUN_ENV);
                    break;
                default:
                    languageConfig.setRunEnvs(DEFAULT_ENV);
            }
        }
        return languageConfig;
    }


    private static Long parseTimeStr(String timeStr) {
        if (StrUtil.isBlank(timeStr)) {
            return 3000L;
        }
        timeStr = timeStr.toLowerCase();
        if (timeStr.endsWith("s")) {
            return Long.parseLong(timeStr.replace("s", "")) * 1000;
        } else if (timeStr.endsWith("ms")) {
            return Long.parseLong(timeStr.replace("s", ""));
        } else {
            return Long.parseLong(timeStr);
        }
    }

    private static Long parseMemoryStr(String memoryStr) {
        if (StrUtil.isBlank(memoryStr)) {
            return 256 * 1024 * 1024L;
        }
        memoryStr = memoryStr.toLowerCase();
        if (memoryStr.endsWith("mb")) {
            return Long.parseLong(memoryStr.replace("mb", "")) * 1024 * 1024;
        } else if (memoryStr.endsWith("kb")) {
            return Long.parseLong(memoryStr.replace("kb", "")) * 1024;
        } else if (memoryStr.endsWith("b")) {
            return Long.parseLong(memoryStr.replace("b", ""));
        } else {
            return Long.parseLong(memoryStr) * 1024 * 1024;
        }
    }
}
