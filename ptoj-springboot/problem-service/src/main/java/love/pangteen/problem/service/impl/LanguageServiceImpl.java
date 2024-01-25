package love.pangteen.problem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import love.pangteen.api.enums.RemoteOJ;
import love.pangteen.problem.constants.OJConstants;
import love.pangteen.problem.mapper.LanguageMapper;
import love.pangteen.problem.pojo.entity.Language;
import love.pangteen.problem.pojo.entity.Problem;
import love.pangteen.problem.service.LanguageService;
import love.pangteen.problem.service.ProblemService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/25 9:58
 **/
@Service
public class LanguageServiceImpl extends ServiceImpl<LanguageMapper, Language> implements LanguageService {

    @Resource
    private ProblemService problemService;

    @Override
    public List<Language> getLanguages(Long pid, boolean all) {
        String oj = OJConstants.DEFAULT_OJ;
        if (pid != null) {
            Problem problem = problemService.getById(pid);
            if (problem.getIsRemote()) {
                oj = problem.getProblemId().split("-")[0];
            }
            if (oj.equals(RemoteOJ.GYM.getName())) {  // GYM用与CF一样的编程语言列表
                oj = RemoteOJ.CODEFORCES.getName();
            }
        }
        return lambdaQuery()
                .eq(!all, Language::getOj, oj).list().stream()
                .sorted(Comparator.comparing(Language::getSeq, Comparator.reverseOrder()).thenComparing(Language::getId))
                .collect(Collectors.toList());
    }
}
