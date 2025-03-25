package project.api.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.api.rest.entity.Permission;
import project.api.rest.entity.Role;

import java.io.Serializable;
import java.time.Instant;
import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
public class UserDTO implements Serializable {

    public UserDTO(String name, String surname, String password, String email, Set<String> roles, Set<Permission> permissions, Instant createdAt, Instant updatedAt) {
        this.name = name;
        this.surname = surname;
        this.password = password;
        this.email = email;
        this.roles = roles;
        this.permissions = permissions;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @Size(max = 50, message = "Name cannot exceed 50 characters")
    @NotNull(message="name is required")
    @NotBlank
    private String name;

    @NotNull(message="surname is required")
    @NotBlank
    private String surname;

    @NotNull(message="password is required")
    @NotBlank
    private String password;

    @NotNull(message="email is required")
    @Email(message="Email isn't valid")
    private String email;

    @NotNull(message="role is required")
    @Size(max = 3)
    private Set<String> roles;

    private Set<Permission> permissions;

    private Instant createdAt;

    private Instant updatedAt;


}
