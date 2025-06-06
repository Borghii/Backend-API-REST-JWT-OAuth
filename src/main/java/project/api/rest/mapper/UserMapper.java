package project.api.rest.mapper;

import org.springframework.stereotype.Component;
import project.api.rest.dto.UserDTO;
import project.api.rest.entity.Permission;
import project.api.rest.entity.Role;
import project.api.rest.entity.RoleEnum;
import project.api.rest.entity.User;

import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Component
public class UserMapper {


    public User toEntity(UserDTO userDTO) {
        User user = new User();
        user.setName(userDTO.getName());
        user.setSurname(userDTO.getSurname());
        user.setPassword(userDTO.getPassword());
        user.setEmail(userDTO.getEmail());


        user.setRoles(userDTO.getRoles().stream()
                .map(role -> new Role(RoleEnum.valueOf(role)))
                .collect(Collectors.toSet()));

        return user;
    }


    public UserDTO toDTO(User user) {
        return UserDTO.builder()
                .name(user.getName())
                .surname(user.getSurname())
                .email(user.getEmail())
                .password("****")


                //I don't want to expose roles
//                .roles(user.getRoles())

                .roles(user.getRoles().stream()
                        .map(role -> role.getRoleEnum().name())
                        .collect(Collectors.toSet()))

                .permissions(user.getRoles().stream()
                        .flatMap(role -> role.getPermissions().stream())
                        .map(Permission::getPermissionName)
                        .collect(Collectors.toSet()))

                .createdAt(user.getCreatedAt().truncatedTo(ChronoUnit.SECONDS))
                .updatedAt(user.getUpdatedAt().truncatedTo(ChronoUnit.SECONDS))
                .build();
    }

}
