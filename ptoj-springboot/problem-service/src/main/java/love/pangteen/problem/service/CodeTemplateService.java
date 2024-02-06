package love.pangteen.problem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import love.pangteen.problem.pojo.entity.CodeTemplate;
import love.pangteen.problem.pojo.entity.Language;

import java.util.HashMap;
import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/22 15:23
 **/
public interface CodeTemplateService extends IService<CodeTemplate> {
    void saveCodeTemplates(Long pid, List<CodeTemplate> codeTemplates);

    void updateCodeTemplates(Long pid, List<CodeTemplate> codeTemplates);

    void deleteCodeTemplates(Long pid);

    List<CodeTemplate> getProblemCodeTemplate(Long pid);

    HashMap<String, String> getLangTemplateMap(Long pid, List<Language> languages);
}
