package love.pangteen.judge.pojo.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/31 16:48
 **/
@Data
@Builder
public class TestCaseDTO {

    private Long caseId;

    private String input;

    private String output;

    private Integer score;

    private Integer groupNum;

}
