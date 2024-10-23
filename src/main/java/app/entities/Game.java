package app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@NamedQueries({
        @NamedQuery(name = "Game.getAll", query = "SELECT g FROM Game g")
})
@Table(name = "game")
public class Game {

    @Id
    @Basic(optional = false)
    @Column(unique = true, nullable = false)
    private Long id;

    @Basic(optional = false)
    private String title;

    @Basic(optional = false)
    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(name = "background_image_url")
    private String backgroundImageURL;

    @Column(name = "meta_critic_score")
    private Integer metaCriticScore;

    private Integer playtime;

    private String description;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "game_platform",
            joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "platform_id")
    )
    private Set<Platform> platformSet;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "game_genre",
            joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genreSet;

}