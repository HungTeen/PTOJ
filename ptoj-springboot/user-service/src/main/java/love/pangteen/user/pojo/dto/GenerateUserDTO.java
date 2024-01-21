package love.pangteen.user.pojo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @program: PTOJ
 * @author: PangTeen
 * @create: 2024/1/21 16:32
 **/
@Data
public class GenerateUserDTO {

    @NotNull(message = "Null prefix")
    String prefix;

    @NotNull(message = "Null suffix")
    String suffix;

    @NotNull(message = "Null numberFrom")
    @JsonProperty(value = "number_from")
    int numberFrom;

    @NotNull(message = "Null numberTo")
    @JsonProperty(value = "number_to")
    int numberTo;

    @NotNull(message = "Null passwordLength")
    @JsonProperty(value = "password_length")
    int passwordLength;

}
