package app.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameFilter {

    private Integer pageNumber;
    private Integer pageSize;

    private String title;
    private LocalDate releaseDateGTE;
    private LocalDate releaseDateLTE;
}
