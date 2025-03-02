package project.api.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

@Builder
@Getter
@Setter
@NoArgsConstructor
public class UserDTO implements Serializable {

    public UserDTO(String name, String surname, String email, Instant createdAt, Instant updatedAt) {
        this.name = name;
        this.surname = surname;
        this.email = email;
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

    @NotNull(message="email is required")
    @Email(message="Email isn't valid")
    private String email;

    private Instant createdAt;

    private Instant updatedAt;


}
