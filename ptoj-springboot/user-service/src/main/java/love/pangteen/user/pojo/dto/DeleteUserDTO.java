package love.pangteen.user.pojo.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/21 16:32
 **/
@Data
public class DeleteUserDTO {

    @NotNull(message = "Null ids")
    List<String> ids;

}
