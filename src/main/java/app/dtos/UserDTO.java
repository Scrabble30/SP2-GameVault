package app.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private String username;
    private String password;
    private Set<String> roles;

    public UserDTO(String username, String password) {
        this(username, password, new HashSet<>());
    }

    public UserDTO(String username, Set<String> roles) {
        this(username, null, roles);
    }
}
