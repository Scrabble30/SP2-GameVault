package app.filter;

import app.entity.Game;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GamePage {

    private Integer pageNumber;
    private Long totalPages;
    private Long totalResults;
    private Set<Game> gameSet;
}
