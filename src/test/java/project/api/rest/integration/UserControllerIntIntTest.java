package project.api.rest.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import project.api.rest.ContainerDB.MySQLContainerBaseIntTest;
import project.api.rest.dto.UserDTO;
import project.api.rest.entity.User;
import project.api.rest.mapper.UserMapper;
import project.api.rest.service.TokenService;
import project.api.rest.service.UserService;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static project.api.rest.constants.TestConstants.*;


//@Testcontainers
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerIntIntTest extends MySQLContainerBaseIntTest {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate; // Para reiniciar el contador de IDs

    private String authToken;

//    @Container
//    @ServiceConnection
//    private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.4.2")
//            .withDatabaseName("testdb")
//            .withUsername("root")
//            .withPassword("password")
//            .withInitScript("schema.sql");

    @BeforeEach
    void setUp() {

        User user = userService.createUser(userMapper.toEntity(USER_DTO));

        authToken = tokenService.generateToken(new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                "password",
                Set.of(new SimpleGrantedAuthority("READ"),
                        new SimpleGrantedAuthority("CREATE"),
                        new SimpleGrantedAuthority("UPDATE"),
                        new SimpleGrantedAuthority("DELETE"))
        ));
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("ALTER TABLE users AUTO_INCREMENT = 1");
    }

    @Test
    void connectionEstablished() {
        assertThat(mysqlContainer.isRunning()).isTrue();
    }

    @Nested
    class GetUserTests {
        @Test
        void testGetAllUsers() throws Exception {

            //when/then
            mockMvc.perform(get(ENDPOINT_USERS)
                            .contentType(APPLICATION_JSON)
                            .header("Authorization", "Bearer " + authToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].name").value("Test"))
                    .andExpect(jsonPath("$[0].surname").value("Test"))
                    .andExpect(jsonPath("$[0].email").value("test@gmail.com"))
                    .andDo(print());
        }

        @Test
        void shouldReturnUserById() throws Exception {
//            //given
//            userService.createUser(userMapper.toEntity(USER_DTO_2));

            // when/then
            mockMvc.perform(get(ENDPOINT_USERS + "/" + 1)
                            .contentType(APPLICATION_JSON)
                            .header("Authorization", "Bearer " + authToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value("test@gmail.com"))
                    .andDo(print());
        }

        @Test
        void shouldThrow404UserNotFound() throws Exception {

            // when/then
            mockMvc.perform(get(ENDPOINT_USERS + "/" + 999)
                            .contentType(APPLICATION_JSON)
                            .header("Authorization", "Bearer " + authToken))
                    .andExpect(status().is4xxClientError())
                    .andExpect(jsonPath("$.message").value("User with id: " + 999 + " not found"))
                    .andDo(print());

        }

    }

    @Nested
    class CreateUserTests {
        @Test
        void shouldCreateUser() throws Exception {
            // given/when
            mockMvc.perform(post(ENDPOINT_USERS)
                            .contentType(APPLICATION_JSON)
                            .header("Authorization", "Bearer " + authToken)
                            .content(new ObjectMapper().writeValueAsString(USER_DTO_2)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value("Test"))
                    .andExpect(jsonPath("$.surname").value("Test"))
                    .andExpect(jsonPath("$.email").value("test2@gmail.com"))
                    .andDo(print());

            // then

            mockMvc.perform(get(ENDPOINT_USERS)
                            .contentType(APPLICATION_JSON)
                            .header("Authorization", "Bearer " + authToken))

                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[1].name").value("Test"))
                    .andExpect(jsonPath("$[1].surname").value("Test"))
                    .andExpect(jsonPath("$[1].email").value("test2@gmail.com"))
                    .andDo(print());


        }

        @Test
        void shouldThrow400UserAlreadyExist() throws Exception {
            // when/then
            mockMvc.perform(post(ENDPOINT_USERS)
                            .contentType(APPLICATION_JSON)
                            .header("Authorization", "Bearer " + authToken)
                            .content(new ObjectMapper().writeValueAsString(USER_DTO)))
                    .andExpect(status().is4xxClientError())
                    .andExpect(jsonPath("$.message").value("User with email " + USER_DTO.getEmail() + " already exists"))
                    .andDo(print());
        }

        @Test
        void shouldThrowMethodArgumentNotValidException() throws Exception {

            //GIVEN
            UserDTO userDTO1 = UserDTO.builder()
                    .name("qwerewrweqrrerqwereqwrewreqwrqewrerqewrewqrerqwerqwereqreqrewrrqe")
                    .surname(null)
                    .email("test")
                    .build();

            //WHEN - THEN
            mockMvc.perform(post(ENDPOINT_USERS)
                            .contentType(APPLICATION_JSON)
                            .header("Authorization", "Bearer " + authToken)
                            .content(new ObjectMapper().writeValueAsString(userDTO1)))
                    .andExpect(status().is4xxClientError())
                    .andExpect(jsonPath("$.details.email").value("Email isn't valid"))
                    .andExpect(jsonPath("$.details.name").value("Name cannot exceed 50 characters"))
                    .andExpect(jsonPath("$.message").value("One or more fields are invalid."))
                    .andDo(print());

        }


    }

    @Nested
    class DeleteUserTests {
        @Test
        void shouldDeleteUser() throws Exception {

            //given
            int id = 1;

            // when/then
            mockMvc.perform(get(ENDPOINT_USERS + "/" + id)
                            .contentType(APPLICATION_JSON)
                            .header("Authorization", "Bearer " + authToken))
                    .andExpect(status().isOk())
                    .andDo(print());

            mockMvc.perform(delete(ENDPOINT_USERS + "/" + id)
                            .contentType(APPLICATION_JSON)
                            .header("Authorization", "Bearer " + authToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").value("User with id : " + id + " deleted successfully"))
                    .andDo(print());

            // then
            mockMvc.perform(get(ENDPOINT_USERS + "/" + id)
                            .contentType(APPLICATION_JSON)
                            .header("Authorization", "Bearer " + authToken))
                    .andExpect(status().is4xxClientError())
                    .andDo(print());

        }

        @Test
        void deleteUserTwiceShouldReturn404() throws Exception {
            int id = 1;
            mockMvc.perform(delete(ENDPOINT_USERS + "/" + id)
                            .header("Authorization", "Bearer " + authToken))
                    .andExpect(status().isOk());

            mockMvc.perform(delete(ENDPOINT_USERS + "/" + id)
                            .header("Authorization", "Bearer " + authToken))
                    .andExpect(status().isNotFound());
        }

        @Test
        void shouldThrow404UserNotFoundToDelete() throws Exception {

            mockMvc.perform(delete(ENDPOINT_USERS + "/999")
                            .contentType(APPLICATION_JSON)
                            .header("Authorization", "Bearer " + authToken))
                    .andExpect(status().is4xxClientError())
                    .andExpect(jsonPath("$.message").value("User with id: " + 999 + " not found"))
                    .andDo(print());

        }
    }

    @Nested
    class UpdateUserTests {
        @Test
        void shouldUpdateUserById() throws Exception {

            //given
            int id = 1;

            // when/then
            mockMvc.perform(put(ENDPOINT_USERS + "/" + id)
                            .contentType(APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(USER_DTO_2))
                            .header("Authorization", "Bearer " + authToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value("test2@gmail.com"))
                    .andDo(print());

            // then
            mockMvc.perform(get(ENDPOINT_USERS + "/" + id)
                            .contentType(APPLICATION_JSON)
                            .header("Authorization", "Bearer " + authToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value("test2@gmail.com"))
                    .andDo(print());

        }


        @Test
        void shouldThrow400UserWithEmailAlreadyExistToUpdate() throws Exception {

            //given
            userService.createUser(userMapper.toEntity(USER_DTO_2));
            int id = 2;

            UserDTO updateDTO = UserDTO.builder()
                    .name(USER_DTO_2.getName())
                    .surname(USER_DTO_2.getSurname())
                    .email("test@gmail.com")
                    .password("test")
                    .roles(Set.of("ADMIN"))
                    .build();

            // when/then
            mockMvc.perform(put(ENDPOINT_USERS + "/" + id)
                            .contentType(APPLICATION_JSON)
                            .header("Authorization", "Bearer " + authToken)
                            .content(new ObjectMapper().writeValueAsString(updateDTO)))
                    .andExpect(status().is4xxClientError())
                    .andExpect(jsonPath("$.message").value("User with email " + USER_DTO.getEmail() + " already exists"))
                    .andDo(print());


        }

        @Test
        void shouldThrow404UserNotFoundToUpdate() throws Exception {

            //given
            int id = 999;

            // when/then
            mockMvc.perform(put(ENDPOINT_USERS + "/" + id)
                            .contentType(APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(USER_DTO_2))
                            .header("Authorization", "Bearer " + authToken))
                    .andExpect(status().is4xxClientError())
                    .andExpect(jsonPath("$.message").value("User with id: " + 999 + " not found"))
                    .andDo(print());


        }


    }

    @Nested
    class UnauthorizedAccessTests {

        @Test
        void getAllUsersWithoutTokenShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(get(ENDPOINT_USERS)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void getUserByIdWithoutTokenShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(get(ENDPOINT_USERS + "/1")
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void createUserWithoutTokenShouldReturnUnauthorized() throws Exception {


            mockMvc.perform(post(ENDPOINT_USERS)
                            .contentType(APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(USER_DTO)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void updateUserWithoutTokenShouldReturnUnauthorized() throws Exception {

            mockMvc.perform(put(ENDPOINT_USERS + "/1")
                            .contentType(APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(USER_DTO)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void deleteUserWithoutTokenShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(delete(ENDPOINT_USERS + "/1")
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());
        }
    }


}