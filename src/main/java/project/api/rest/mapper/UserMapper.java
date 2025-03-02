package project.api.rest.mapper;

import org.springframework.stereotype.Component;
import project.api.rest.dto.UserDTO;
import project.api.rest.entity.User;

import java.time.temporal.ChronoUnit;

@Component
public class UserMapper {
    public User toEntity(UserDTO userDTO){
        User user = new User();
        user.setName(userDTO.getName());
        user.setSurname(userDTO.getSurname());
        user.setEmail(userDTO.getEmail());
        return user;
    }

    public UserDTO toDTO(User user){
        return UserDTO.builder()
                .name(user.getName())
                .surname(user.getSurname())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt().truncatedTo(ChronoUnit.SECONDS))
                .updatedAt(user.getUpdatedAt().truncatedTo(ChronoUnit.SECONDS))
                .build();
    }

}
