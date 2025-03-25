package project.api.rest.mapper;

import org.springframework.stereotype.Component;
import project.api.rest.dto.UserDTO;
import project.api.rest.entity.Role;
import project.api.rest.entity.RoleEnum;
import project.api.rest.entity.User;

import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper {




    public User toEntity(UserDTO userDTO){
        User user = new User();
        user.setName(userDTO.getName());
        user.setSurname(userDTO.getSurname());
        user.setPassword(userDTO.getPassword());
        user.setEmail(userDTO.getEmail());


        user.setRoles(userDTO.getRoles().stream()
                .map(roleName -> new Role(RoleEnum.valueOf(roleName)))
                .collect(Collectors.toSet()));

        return user;
    }

    public UserDTO toDTO(User user){
        return UserDTO.builder()
                .name(user.getName())
                .surname(user.getSurname())
                .password(user.getPassword())
                .email(user.getEmail())

                .roles(user.getRoles().stream()
                        .map(role -> role.getRoleEnum().name())
                        .collect(Collectors.toSet()))

                .permissions(user.getRoles().stream()
                        .flatMap(role -> role.getPermissions().stream())
                        .collect(Collectors.toSet()))

                .createdAt(user.getCreatedAt().truncatedTo(ChronoUnit.SECONDS))
                .updatedAt(user.getUpdatedAt().truncatedTo(ChronoUnit.SECONDS))
                .build();
    }

}
