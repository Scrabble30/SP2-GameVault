package app.dtos;

import lombok.*;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {

    private Long id;
    private Long userId;
    private Long gameId;
    private Double rating;
    private String review;

}
