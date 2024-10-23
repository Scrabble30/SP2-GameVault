package app.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GameDTO {

    private Long id;

    @JsonProperty("name")
    private String title;

    @JsonProperty("released")
    private LocalDate releaseDate;

    @JsonProperty("background_image")
    private String backgroundImageURL;

    @JsonProperty("metacritic")
    private Integer metaCriticScore;

    private Integer playtime;

    private String description;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Double rating;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer ratingCount;

    @JsonProperty("parent_platforms")
    private Set<ParentPlatFormDTO> parentPlatformDTOSet;

    @JsonProperty("genres")
    private Set<GenreDTO> genreDTOSet;

    public Set<PlatformDTO> getPlatformDTOSet() {
        return parentPlatformDTOSet.stream().map(parent -> new PlatformDTO(parent.getPlatformDTO().getId(), parent.getPlatformDTO().getName())).collect(Collectors.toSet());
    }
}
