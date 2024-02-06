package love.pangteen.problem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import love.pangteen.problem.pojo.entity.Language;

import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/25 9:58
 **/
public interface LanguageService extends IService<Language> {

    List<Language> getLanguages(String oj, boolean all);

    List<Language> getLanguages(List<Long> lids);
}
