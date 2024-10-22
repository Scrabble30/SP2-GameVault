package app.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ParentPlatFormDTO {

    @JsonProperty("platform")
    private PlatformDTO platformDTO;

}
