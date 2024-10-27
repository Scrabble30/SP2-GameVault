package app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GameDTO {

    private Long id;

    @JsonProperty("name")
    private String title;

    @JsonProperty("released")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate releaseDate;

    @JsonProperty("background_image")
    private String backgroundImageURL;

    @JsonProperty("metacritic")
    private Integer metaCriticScore;

    private Integer playtime;

    private String description;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Double rating;

    @JsonProperty(value = "rating_count", access = JsonProperty.Access.READ_ONLY)
    private Long ratingCount;

    @JsonProperty("platforms")
    private Set<PlatformDTO> platformDTOSet;

    @JsonProperty("genres")
    private Set<GenreDTO> genreDTOSet;
}
