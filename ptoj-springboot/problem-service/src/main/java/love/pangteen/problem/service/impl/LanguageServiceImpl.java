package love.pangteen.problem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import love.pangteen.problem.mapper.LanguageMapper;
import love.pangteen.api.pojo.entity.Language;
import love.pangteen.problem.service.LanguageService;
import org.springframework.stereotype.Service;

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

    @Override
    public List<Language> getLanguages(String oj, boolean all) {
        return lambdaQuery()
                .eq(!all, Language::getOj, oj).list().stream()
                .sorted(Comparator.comparing(Language::getSeq, Comparator.reverseOrder()).thenComparing(Language::getId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Language> getLanguages(List<Long> lids) {
        return listByIds(lids).stream().sorted(Comparator.comparing(Language::getSeq, Comparator.reverseOrder())
                        .thenComparing(Language::getId))
                .collect(Collectors.toList());
    }
}
