package app.dto;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {

    private Long id;
    private String username;
    private Long gameId;
    private Double rating;
    private String review;

}
