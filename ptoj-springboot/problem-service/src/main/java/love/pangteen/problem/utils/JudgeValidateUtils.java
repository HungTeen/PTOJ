package love.pangteen.problem.utils;

import cn.hutool.core.util.StrUtil;
import love.pangteen.api.enums.OJAccessType;
import love.pangteen.exception.OJAccessException;
import love.pangteen.exception.StatusFailException;
import love.pangteen.problem.pojo.dto.SubmitJudgeDTO;
import love.pangteen.problem.pojo.dto.TestJudgeDTO;
import love.pangteen.utils.AccessUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/26 15:03
 **/
@Component
public class JudgeValidateUtils {

    @Resource
    private AccessUtils accessUtils;

    private final List<String> languageList = Arrays.asList(
            "C++", "C++ With O2", "C++ 17", "C++ 17 With O2","C++ 20", "C++ 20 With O2",
            "C", "C With O2", "Python3", "Python2", "Java", "Golang", "C#", "PHP", "PyPy2", "PyPy3",
            "JavaScript Node", "JavaScript V8", "Ruby", "Rust");

    private final HashMap<String, String> languageMap = new HashMap<>();

    @PostConstruct
    public void init() {
        languageMap.clear();
        languageMap.put("text/x-c++src", "C++ With O2");
        languageMap.put("text/x-csrc", "C With O2");
        languageMap.put("text/x-java", "Java");
        languageMap.put("text/x-go", "Golang");
        languageMap.put("text/x-csharp", "C#");
        languageMap.put("text/x-php", "PHP");
        languageMap.put("text/x-ruby", "Ruby");
        languageMap.put("text/x-rustsrc", "Rust");
    }

    public void validateSubmission(SubmitJudgeDTO submitJudgeDto) throws StatusFailException, OJAccessException {
        if (submitJudgeDto.getGid() != null) { // 团队内的提交
            accessUtils.validateAccess(OJAccessType.GROUP_JUDGE);
        } else if (submitJudgeDto.getCid() != null && submitJudgeDto.getCid() != 0) {
            accessUtils.validateAccess(OJAccessType.CONTEST_JUDGE);
        } else {
            accessUtils.validateAccess(OJAccessType.PUBLIC_JUDGE);
        }

        if (!submitJudgeDto.getIsRemote() && !languageList.contains(submitJudgeDto.getLanguage())) {
            throw new StatusFailException("提交的代码的语言错误！请使用" + languageList + "中之一的语言！");
        }

        if (submitJudgeDto.getCode().length() < 50
                && !submitJudgeDto.getLanguage().contains("Py")
                && !submitJudgeDto.getLanguage().contains("PHP")
                && !submitJudgeDto.getLanguage().contains("Ruby")
                && !submitJudgeDto.getLanguage().contains("JavaScript")) {
            throw new StatusFailException("提交的代码是无效的，代码字符长度请不要低于50！");
        }

        if (submitJudgeDto.getCode().length() > 65535) {
            throw new StatusFailException("提交的代码是无效的，代码字符长度请不要超过65535！");
        }
    }

    public void validateTestJudge(TestJudgeDTO testJudgeDto) throws StatusFailException, OJAccessException {
        String type = testJudgeDto.getType();
        switch (type) {
            case "public":
                accessUtils.validateAccess(OJAccessType.PUBLIC_JUDGE);
                break;
            case "contest":
                accessUtils.validateAccess(OJAccessType.CONTEST_JUDGE);
                break;
            case "group":
                accessUtils.validateAccess(OJAccessType.GROUP_JUDGE);
                break;
            default:
                throw new StatusFailException("请求参数type错误！");
        }

        if (StrUtil.isEmpty(testJudgeDto.getCode())) {
            throw new StatusFailException("在线调试的代码不可为空！");
        }

        if (StrUtil.isEmpty(testJudgeDto.getLanguage())) {
            throw new StatusFailException("在线调试的编程语言不可为空！");
        }

        // Remote Judge的编程语言需要转换成HOJ的编程语言才能进行自测
        if (testJudgeDto.getIsRemoteJudge() != null && testJudgeDto.getIsRemoteJudge()) {
            String language = languageMap.get(testJudgeDto.getMode());
            if (language != null) {
                testJudgeDto.setLanguage(language);
            } else {
                String dtoLanguage = testJudgeDto.getLanguage();
                if (dtoLanguage.contains("PyPy 3") || dtoLanguage.contains("PyPy3")) {
                    testJudgeDto.setLanguage("PyPy3");
                } else if (dtoLanguage.contains("PyPy")) {
                    testJudgeDto.setLanguage("PyPy2");
                } else if (dtoLanguage.contains("Python 3")) {
                    testJudgeDto.setLanguage("Python3");
                } else if (dtoLanguage.contains("Python")) {
                    testJudgeDto.setLanguage("Python2");
                }else if (dtoLanguage.contains("Node")){
                    testJudgeDto.setLanguage("JavaScript Node");
                }else if (dtoLanguage.contains("JavaScript")){
                    testJudgeDto.setLanguage("JavaScript V8");
                }
            }
        }

        if (!languageList.contains(testJudgeDto.getLanguage())) {
            throw new StatusFailException("提交的代码的语言错误！请使用" + languageList + "中之一的语言！");
        }

        if (StrUtil.isEmpty(testJudgeDto.getUserInput())) {
            throw new StatusFailException("在线调试的输入数据不可为空！");
        }

        if (testJudgeDto.getUserInput().length() > 1000) {
            throw new StatusFailException("在线调试的输入数据字符长度不能超过1000！");
        }

        if (testJudgeDto.getPid() == null) {
            throw new StatusFailException("在线调试所属题目的id不能为空！");
        }

        if (testJudgeDto.getCode().length() < 50
                && !testJudgeDto.getLanguage().contains("Py")
                && !testJudgeDto.getLanguage().contains("PHP")
                && !testJudgeDto.getLanguage().contains("Ruby")
                && !testJudgeDto.getLanguage().contains("JavaScript")) {
            throw new StatusFailException("提交的代码是无效的，代码字符长度请不要低于50！");
        }

        if (testJudgeDto.getCode().length() > 65535) {
            throw new StatusFailException("提交的代码是无效的，代码字符长度请不要超过65535！");
        }

    }
}
