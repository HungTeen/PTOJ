package love.pangteen.problem.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import love.pangteen.problem.mapper.CodeTemplateMapper;
import love.pangteen.problem.pojo.entity.CodeTemplate;
import love.pangteen.problem.service.CodeTemplateService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/22 15:24
 **/
@Service
public class CodeTemplateServiceImpl extends ServiceImpl<CodeTemplateMapper, CodeTemplate> implements CodeTemplateService {

    @Transactional
    @Override
    public void saveCodeTemplates(Long pid, List<CodeTemplate> codeTemplates) {
        if (CollUtil.isNotEmpty(codeTemplates)) {
            codeTemplates.forEach(codeTemplate -> codeTemplate.setPid(pid));
            saveBatch(codeTemplates);
        }
    }

    @Transactional
    @Override
    public void updateCodeTemplates(Long pid, List<CodeTemplate> codeTemplates) {
        Set<Integer> oldIds = lambdaQuery()
                .eq(CodeTemplate::getPid, pid).list().stream()
                .map(CodeTemplate::getId).collect(Collectors.toSet());
        Set<Integer> ids = codeTemplates.stream().map(CodeTemplate::getId).collect(Collectors.toSet());
        List<Integer> invalidIds = oldIds.stream().filter(id -> {
            return !ids.contains(id);
        }).collect(Collectors.toList());
        removeByIds(invalidIds); // 删除多余的。
        codeTemplates.stream().filter(template -> template.getPid() == null).forEach(template -> template.setPid(pid));
        saveOrUpdateBatch(codeTemplates);
    }

    @Override
    public void deleteCodeTemplates(Long pid) {
        remove(new LambdaQueryWrapper<>(CodeTemplate.class).eq(CodeTemplate::getPid, pid));
    }

    @Override
    public List<CodeTemplate> getProblemCodeTemplate(Long pid) {
        return lambdaQuery().eq(CodeTemplate::getPid, pid).list();
    }
}
