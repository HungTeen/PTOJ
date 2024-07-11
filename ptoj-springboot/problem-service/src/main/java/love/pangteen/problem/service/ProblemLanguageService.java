package love.pangteen.problem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import love.pangteen.api.pojo.entity.Language;
import love.pangteen.problem.pojo.entity.ProblemLanguage;

import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/22 15:15
 **/
public interface ProblemLanguageService extends IService<ProblemLanguage> {
    void saveProblemLanguages(Long pid, List<Language> languages);

    void updateProblemLanguages(Long pid, List<Language> languages);

    void deleteProblemLanguages(Long pid);

    List<Language> getLanguages(Long pid);

    List<ProblemLanguage> getProblemLanguages(Long pid);
}
