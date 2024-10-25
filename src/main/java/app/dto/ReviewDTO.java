package app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {

    private Long id;

    private String username;

    @JsonProperty("game_id")
    private Long gameId;

    private Double rating;

    private String review;

}
