package project.api.rest.constants;

import project.api.rest.dto.UserDTO;
import project.api.rest.entity.Permission;
import project.api.rest.entity.Role;
import project.api.rest.entity.RoleEnum;
import project.api.rest.entity.User;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

public class TestConstants {

    public static final String ENDPOINT_USERS = "/api/v1/users";
    public static final String ENDPOINT_AUTH = "/api/v1/auth";

    public static final UserDTO USER_DTO = UserDTO.builder()
            .name("Test")
            .surname("Test")
            .email("test@gmail.com")
            .password("Test")
            .roles(new HashSet<>(Set.of("ADMIN"))).build();

    public static final UserDTO USER_DTO_2 = UserDTO.builder()
            .name("Test")
            .surname("Test")
            .email("test2@gmail.com")
            .password("Test2")
            .roles(new HashSet<>(Set.of("ADMIN"))).build();

    public static final User USER = User.builder()
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

    private static final Set<Permission> PERMISSIONS = Set.of(
            Permission.builder().permissionName("READ").build(),
            Permission.builder().permissionName("CREATE").build(),
            Permission.builder().permissionName("DELETE").build(),
            Permission.builder().permissionName("UPDATE").build());

}
