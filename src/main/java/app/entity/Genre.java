package app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@NamedQueries({
        @NamedQuery(name = "Genre.getAll", query = "SELECT g FROM Genre g")
})
@Table(name = "genre")
public class Genre {

    @Id
    @Basic(optional = false)
    @Column(unique = true, nullable = false)
    private Long id;

    @Basic(optional = false)
    @Column(unique = true, nullable = false, length = 20)
    private String name;

}
