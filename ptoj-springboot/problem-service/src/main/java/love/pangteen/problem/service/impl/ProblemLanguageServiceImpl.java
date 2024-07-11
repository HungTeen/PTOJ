package love.pangteen.problem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import love.pangteen.problem.mapper.ProblemLanguageMapper;
import love.pangteen.api.pojo.entity.Language;
import love.pangteen.problem.pojo.entity.ProblemLanguage;
import love.pangteen.problem.service.LanguageService;
import love.pangteen.problem.service.ProblemLanguageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/22 15:16
 **/
@Service
public class ProblemLanguageServiceImpl extends ServiceImpl<ProblemLanguageMapper, ProblemLanguage> implements ProblemLanguageService {

    @Resource
    private LanguageService languageService;

    @Transactional
    @Override
    public void saveProblemLanguages(Long pid, List<Language> languages) {
        List<ProblemLanguage> problemLanguageList = languages.stream().map(language -> {
            return new ProblemLanguage().setPid(pid).setLid(language.getId());
        }).collect(Collectors.toList());
        saveBatch(problemLanguageList);
    }

    @Transactional
    @Override
    public void updateProblemLanguages(Long pid, List<Language> languages) {
        List<ProblemLanguage> oldLanguages = lambdaQuery().eq(ProblemLanguage::getPid, pid).list();
        Set<Long> oldLanguageIds = oldLanguages.stream().map(ProblemLanguage::getLid).collect(Collectors.toSet());
        Set<Long> languageIds = languages.stream().map(Language::getId).collect(Collectors.toSet());
        List<Long> invalidIds = oldLanguages.stream().filter(language -> {
            return !languageIds.contains(language.getLid());
        }).map(ProblemLanguage::getId).collect(Collectors.toList());
        removeByIds(invalidIds);
        saveProblemLanguages(pid, languages.stream().filter(language -> {
            return !oldLanguageIds.contains(language.getId());
        }).collect(Collectors.toList()));
    }

    @Override
    public void deleteProblemLanguages(Long pid) {
        remove(new LambdaQueryWrapper<>(ProblemLanguage.class).eq(ProblemLanguage::getPid, pid));
    }

    @Override
    public List<Language> getLanguages(Long pid) {
        List<Long> lids = getProblemLanguages(pid).stream().map(ProblemLanguage::getLid).collect(Collectors.toList());
        return languageService.getLanguages(lids);
    }

    @Override
    public List<ProblemLanguage> getProblemLanguages(Long pid) {
        return lambdaQuery().eq(ProblemLanguage::getPid, pid).list();
    }

}
