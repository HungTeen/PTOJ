package love.pangteen.problem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import love.pangteen.problem.pojo.entity.ProblemCase;

import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/22 17:01
 **/
public interface ProblemCaseService extends IService<ProblemCase> {
    List<ProblemCase> getProblemCases(Long pid, Boolean isUpload);
}
