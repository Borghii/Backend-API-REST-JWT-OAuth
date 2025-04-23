package project.api.rest.integration;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import project.api.rest.ContainerDB.MySQLContainerBaseIntTest;
import project.api.rest.constants.TestConstants;
import project.api.rest.dto.UserDTO;
import project.api.rest.mapper.UserMapper;
import project.api.rest.service.UserService;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static project.api.rest.constants.TestConstants.USER_DTO;


//@Testcontainers
//@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = "test")
class AuthControllerIntTest extends MySQLContainerBaseIntTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate; // Para reiniciar el contador de IDs

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("ALTER TABLE users AUTO_INCREMENT = 1");
    }


    @Nested
    class SignUp{
        @Test
        void shouldCreateUser() throws Exception {

            mockMvc.perform(post(TestConstants.ENDPOINT_AUTH+"/sign-up")
                            .contentType(APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(USER_DTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.userDTO.email").value("test@gmail.com"))
                    .andExpect(jsonPath("$.userDTO.permissions.length()").value(4))
                    .andExpect(jsonPath("$.token").exists());

        }

        @Test
        void shouldTrow400BadRequest() throws Exception {

            UserDTO userDTO2 = UserDTO.builder()
                    .name("Test")
                    .surname("Test")
                    .build();

            mockMvc.perform(post(TestConstants.ENDPOINT_AUTH+"/sign-up")
                            .contentType(APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(userDTO2)))
                    .andExpect(status().is4xxClientError())
                    .andExpect(jsonPath("$.message").value("One or more fields are invalid."));


        }

    }

    @Nested
    class Login{

        @Test
        void shouldLoginUser() throws Exception{

            userService.createUser(userMapper.toEntity(USER_DTO));

            mockMvc.perform(post(TestConstants.ENDPOINT_AUTH+"/login")
                            .contentType(APPLICATION_JSON)
                            .with(httpBasic(USER_DTO.getEmail(), USER_DTO.getPassword())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.userDTO.email").value("test@gmail.com"))
                    .andExpect(jsonPath("$.userDTO.permissions.length()").value(4))
                    .andExpect(jsonPath("$.token").exists());
        }

        @Test
        void shouldThrow401Unauthorized() throws Exception{
            mockMvc.perform(post(TestConstants.ENDPOINT_AUTH+"/login")
                            .contentType(APPLICATION_JSON)
                            .with(httpBasic(USER_DTO.getEmail(), USER_DTO.getPassword())))
                    .andExpect(status().isUnauthorized());
        }

    }


}
