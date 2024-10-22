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
@Entity
@Table(name = "platform")
public class Platform {

    @Id
    @Basic(optional = false)
    @Column(unique = true, nullable = false)
    private Long id;

    @Basic(optional = false)
    @Column(nullable = false)
    private String name;

}