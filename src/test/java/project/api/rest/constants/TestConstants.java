package project.api.rest.constants;

import project.api.rest.dto.UserDTO;
import project.api.rest.entity.User;

import java.util.HashSet;
import java.util.Set;

public class TestConstants {

    public static final String ENDPOINT_USERS = "/api/v1/users";
    public static final String ENDPOINT_AUTH = "/api/v1/auth";

    public static final UserDTO userDTO = UserDTO.builder()
            .name("Test")
            .surname("Test")
            .email("test@gmail.com")
            .password("Test")
            .roles(new HashSet<>(Set.of("ADMIN"))).build();

    public static final UserDTO userDTO2 = UserDTO.builder()
            .name("Test")
            .surname("Test")
            .email("test2@gmail.com")
            .password("Test2")
            .roles(new HashSet<>(Set.of("ADMIN"))).build();

}
