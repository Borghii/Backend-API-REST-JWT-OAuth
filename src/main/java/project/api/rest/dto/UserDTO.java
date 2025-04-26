package project.api.rest.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
public class UserDTO implements Serializable {

    @Size(max = 50, message = "Name cannot exceed 50 characters")
    @NotNull(message = "name is required")
    @NotBlank
    private String name;
    @NotNull(message = "surname is required")
    @NotBlank
    private String surname;
    @NotNull(message = "password is required")
    @NotBlank
    private String password;
    @NotNull(message = "email is required")
    @Email(message = "Email isn't valid")
    private String email;
    @NotNull(message = "role is required")
    @NotEmpty
    @Size(max = 3)
    private Set<String> roles;
    private Set<String> permissions;
    private Instant createdAt;
    private Instant updatedAt;

    public UserDTO(String name, String surname, String password, String email, Set<String> roles, Set<String> permissions, Instant createdAt, Instant updatedAt) {
        this.name = name;
        this.surname = surname;
        this.password = password;
        this.email = email;
        this.roles = roles;
        this.permissions = permissions;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


}
