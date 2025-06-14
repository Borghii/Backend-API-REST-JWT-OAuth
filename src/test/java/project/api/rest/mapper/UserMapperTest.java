package project.api.rest.mapper;

import org.junit.jupiter.api.Test;
import project.api.rest.dto.UserDTO;
import project.api.rest.entity.Permission;
import project.api.rest.entity.Role;
import project.api.rest.entity.RoleEnum;
import project.api.rest.entity.User;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static project.api.rest.constants.TestConstants.USER_DTO;

class UserMapperTest {

    private final UserMapper userMapper = new UserMapper();

    @Test
    void toEntity() {
        //give - when
        User user = userMapper.toEntity(USER_DTO);
        //then
        assertThat(user.getName()).isEqualTo(USER_DTO.getName());
        assertThat(user.getSurname()).isEqualTo(USER_DTO.getSurname());
        assertThat(user.getEmail()).isEqualTo(USER_DTO.getEmail());
        assertThat(user.getRoles().stream().map(r -> r.getRoleEnum()
                        .name())
                .collect(Collectors.toSet()))
                .isEqualTo(USER_DTO.getRoles());

    }

    @Test
    void toDTO() {

        //given
        User USER = User.builder()
                .name("Test")
                .surname("Test")
                .email("test2@gmail.com")
                .password("Test2")
                .roles(Set.of(Role.builder()
                        .roleEnum(RoleEnum.valueOf("ADMIN"))
                        .permissions(Set.of(
                                Permission.builder().permissionName("READ").build(),
                                Permission.builder().permissionName("CREATE").build(),
                                Permission.builder().permissionName("DELETE").build(),
                                Permission.builder().permissionName("UPDATE").build()))
                        .build()))
                .createdAt(Instant.MIN)
                .updatedAt(Instant.now())
                .build();


        //when
        UserDTO userDTO = userMapper.toDTO(USER);



        //then
        assertThat(userDTO.getName()).isEqualTo(USER.getName());
        assertThat(userDTO.getSurname()).isEqualTo(USER.getSurname());
        assertThat(userDTO.getEmail()).isEqualTo(USER.getEmail());
        assertThat(userDTO.getPassword()).isEqualTo("****");
        assertThat(userDTO.getRoles()).isEqualTo(Set.of("ADMIN"));
        assertThat(userDTO.getPermissions()).isEqualTo(Set.of("READ", "CREATE", "UPDATE", "DELETE"));
        assertThat(userDTO.getCreatedAt()).isEqualTo(USER.getCreatedAt().truncatedTo(ChronoUnit.SECONDS));
        assertThat(userDTO.getUpdatedAt()).isEqualTo(USER.getUpdatedAt().truncatedTo(ChronoUnit.SECONDS));
    }
}