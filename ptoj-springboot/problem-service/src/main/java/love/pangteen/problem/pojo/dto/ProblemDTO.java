package love.pangteen.problem.pojo.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import love.pangteen.problem.pojo.entity.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/22 9:56
 **/
@Data
@Accessors(chain = true)
public class ProblemDTO {

    @NotNull(message = "题目的配置项不能为空！")
    @Valid
    private Problem problem;

    private List<ProblemCase> samples;

    private Boolean isUploadTestCase;

    private String uploadTestcaseDir;

    private String judgeMode;

    private Boolean changeModeCode;

    private Boolean changeJudgeCaseMode;

    private List<Language> languages;

    private List<Tag> tags;

    private List<CodeTemplate> codeTemplates;
}
