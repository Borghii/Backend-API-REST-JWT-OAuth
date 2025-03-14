package project.api.rest.integration;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.com.github.dockerjava.core.MediaType;
import org.testcontainers.utility.MountableFile;
import project.api.rest.constants.TestConstants;
import project.api.rest.dto.UserDTO;
import project.api.rest.entity.User;
import project.api.rest.mapper.UserMapper;
import project.api.rest.repository.UserRepository;
import project.api.rest.service.UserService;

import static org.antlr.v4.runtime.misc.MurmurHash.update;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static project.api.rest.constants.TestConstants.*;


@ActiveProfiles("test")
@AutoConfigureMockMvc
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class UserControllerIntTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate; // Para reiniciar el contador de IDs

    @Container
    @ServiceConnection
    private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.4.2")
            .withDatabaseName("testdb")
            .withUsername("root")
            .withPassword("password")
            .withInitScript("schema.sql");

    @Test
    void connectionEstablished() {
        assertThat(mysqlContainer.isRunning()).isTrue();
    }

    @BeforeEach
    void setUp() {
        userRepository.deleteAll(); // Elimina los datos
        jdbcTemplate.execute("ALTER TABLE users AUTO_INCREMENT = 1"); // Reinicia el contador de IDs
    }

    @Nested
    class GetUserTests {
        @Test
        void testGetAllUsers() throws Exception {
            //given
            userService.createUser(userMapper.toEntity(userDTO));

            //when/then
            mockMvc.perform(get(ENDPOINT_USERS)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].name").value("Test"))
                    .andExpect(jsonPath("$[0].surname").value("Test"))
                    .andExpect(jsonPath("$[0].email").value("test@gmail.com"))
                    .andDo(print());
        }

        @Test
        void shouldReturnUserById() throws Exception {
            //given
            userService.createUser(userMapper.toEntity(userDTO2));

            // when/then
            mockMvc.perform(get(ENDPOINT_USERS + "/" + 1).contentType(APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value("test2@gmail.com"))
                    .andDo(print());
        }

        @Test
        void shouldThrow404UserNotFound() throws Exception{

            // when/then
            mockMvc.perform(get(ENDPOINT_USERS + "/" + 999).contentType(APPLICATION_JSON))
                    .andExpect(status().is4xxClientError())
                    .andExpect(jsonPath("$.message").value("User with id: " + 999 + " not found"))
                    .andDo(print());

        }

    }

    @Nested
    class CreateUserTests{
        @Test
        void shouldCreateUser() throws Exception {
            // given/when
            mockMvc.perform(post(ENDPOINT_USERS)
                            .contentType(APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(userDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value("Test"))
                    .andExpect(jsonPath("$.surname").value("Test"))
                    .andExpect(jsonPath("$.email").value("test@gmail.com"))
                    .andDo(print());

            // then

            mockMvc.perform(get(ENDPOINT_USERS)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].name").value("Test"))
                    .andExpect(jsonPath("$[0].surname").value("Test"))
                    .andExpect(jsonPath("$[0].email").value("test@gmail.com"))
                    .andDo(print());


        }

        @Test
        void shouldThrow400UserAlreadyExist() throws Exception{

            //given
            userService.createUser(userMapper.toEntity(userDTO));



            // when/then
            mockMvc.perform(post(ENDPOINT_USERS)
                            .contentType(APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(userDTO)))
                    .andExpect(status().is4xxClientError())
                    .andExpect(jsonPath("$.message").value("User with email " + userDTO.getEmail() + " already exists"))
                    .andDo(print());



        }


    }

    @Nested
    class DeleteUserTests {
        @Test
        void shouldDeleteUser() throws Exception {
            //given
            userService.createUser(userMapper.toEntity(userDTO));

            int id = 1;

            // when/then

            mockMvc.perform(get(ENDPOINT_USERS+"/" + id)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print());

            mockMvc.perform(delete(ENDPOINT_USERS + "/" + id)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").value("User with id : "+ id+" deleted successfully"))
                    .andDo(print());

            // then

            mockMvc.perform(get(ENDPOINT_USERS+"/" + id)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().is4xxClientError())
                    .andDo(print());

        }

        @Test
        void shouldThrow404UserNotFoundToDelete() throws Exception{

            mockMvc.perform(delete(ENDPOINT_USERS+"/999")
                    .contentType(APPLICATION_JSON))
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
            userService.createUser(userMapper.toEntity(userDTO));
            int id = 1;

            // when/then
            mockMvc.perform(put(ENDPOINT_USERS+"/"+id)
                            .contentType(APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(userDTO2)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value("test2@gmail.com"))
                    .andDo(print());

            // then

            mockMvc.perform(get(ENDPOINT_USERS+"/"+id)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value("test2@gmail.com"))
                    .andDo(print());

        }

//        @ApiResponse(responseCode = "400", description = "User with email already exist"),
//        @ApiResponse(responseCode = "404", description = "User not found")

        @Test
        void shouldThrow400UserWithEmailAlreadyExistToUpdate() throws Exception {

            //given
            userService.createUser(userMapper.toEntity(userDTO));

            userService.createUser(userMapper.toEntity(userDTO2));
            int id = 2;

           UserDTO updateDTO = UserDTO.builder()
                   .name(userDTO2.getName())
                   .surname(userDTO2.getSurname())
                   .email("test@gmail.com")
                   .build();



            // when/then
            mockMvc.perform(put(ENDPOINT_USERS+"/"+id)
                            .contentType(APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(updateDTO)))
                    .andExpect(status().is4xxClientError())
                    .andExpect(jsonPath("$.message").value("User with email " + userDTO.getEmail() + " already exists"))
                    .andDo(print());



        }

        @Test
        void shouldThrow404UserNotFoundToUpdate() throws Exception {

            //given
            int id = 999;

            // when/then
            mockMvc.perform(put(ENDPOINT_USERS+"/"+id)
                            .contentType(APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(userDTO2)))
                    .andExpect(status().is4xxClientError())
                    .andExpect(jsonPath("$.message").value("User with id: " + 999 + " not found"))
                    .andDo(print());



        }




    }









}