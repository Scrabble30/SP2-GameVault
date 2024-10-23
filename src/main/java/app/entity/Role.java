package app.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@Table(name = "role")
public class Role {

    @Id
    @Basic(optional = false)
    @Column(unique = true, nullable = false)
    private String name;

    @ManyToMany
    private Set<User> users;

    @Builder
    public Role(String name) {
        this.name = name;
        this.users = new HashSet<>();
    }

    public void addUser(User user) {
        this.users.add(user);
        user.getRoles().add(this);
    }

    public void removeUser(User user) {
        this.users.remove(user);
        user.getRoles().remove(this);
    }
}
