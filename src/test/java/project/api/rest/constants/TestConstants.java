package project.api.rest.constants;

import project.api.rest.dto.UserDTO;
import project.api.rest.entity.User;

public class TestConstants {

    public static final String ENDPOINT_USERS = "/api/v1/users";

    public static final UserDTO userDTO = UserDTO.builder()
            .name("Test")
            .surname("Test")
            .email("test@gmail.com").build();

    public static final UserDTO userDTO2 = UserDTO.builder()
            .name("Test")
            .surname("Test")
            .email("test2@gmail.com").build();

}
