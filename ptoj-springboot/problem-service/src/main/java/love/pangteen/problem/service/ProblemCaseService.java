package love.pangteen.problem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import love.pangteen.api.pojo.entity.Problem;
import love.pangteen.api.pojo.entity.ProblemCase;

import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/22 17:01
 **/
public interface ProblemCaseService extends IService<ProblemCase> {
    List<ProblemCase> getProblemCases(Long pid, Boolean isUpload);

    void saveProblemCases(Problem problem, List<ProblemCase> problemCases, boolean isUpload, String uploadTestcaseDir);

    void updateProblemCases(Problem problem, List<ProblemCase> samples, Boolean isUploadTestCase, String uploadTestcaseDir);

    void deleteProblemCases(Long pid);
}
