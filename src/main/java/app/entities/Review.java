package app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@NamedQueries({
        @NamedQuery(name = "Review.getByGameId", query = "SELECT r FROM Review r WHERE r.gameId = :gameId"),
        @NamedQuery(name = "Review.getAll", query = "SELECT r FROM Review r"),
        @NamedQuery(name = "Review.getAverageRating", query = "SELECT AVG(r.rating) FROM Review r WHERE r.gameId = :gameId"),
        @NamedQuery(name = "Review.getRatingCount", query = "SELECT COUNT(r) FROM Review r WHERE r.gameId = :gameId")
})
@Entity
@Table(name = "review")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(unique = true, nullable = false)
    private Long id;

    @Basic(optional = false)
    @Column(nullable = false)
    private String username;

    @Basic(optional = false)
    @Column(nullable = false)
    private Long gameId;

    @Basic(optional = false)
    @Column(nullable = false)
    private Double rating;

    @Basic(optional = false)
    @Column(nullable = false)
    private String review;

}
