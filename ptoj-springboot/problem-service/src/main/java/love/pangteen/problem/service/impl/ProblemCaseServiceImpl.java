package love.pangteen.problem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import love.pangteen.problem.mapper.ProblemCaseMapper;
import love.pangteen.problem.pojo.entity.ProblemCase;
import love.pangteen.problem.service.ProblemCaseService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/22 17:02
 **/
@Service
public class ProblemCaseServiceImpl extends ServiceImpl<ProblemCaseMapper, ProblemCase> implements ProblemCaseService {

    @Override
    public List<ProblemCase> getProblemCases(Long pid, Boolean isUpload) {
        return lambdaQuery()
                .eq(ProblemCase::getPid, pid)
                .eq(ProblemCase::getStatus, 0)
                .last(isUpload, "order by length(input) asc,input asc")
                .list();
    }
}
