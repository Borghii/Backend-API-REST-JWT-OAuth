package project.api.rest.integration;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import project.api.rest.constants.TestConstants;
import project.api.rest.dto.UserDTO;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static project.api.rest.constants.TestConstants.userDTO;
import static project.api.rest.constants.TestConstants.userDTO2;


//@Testcontainers
//@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = "test")
class AuthControllerIntTest extends MySQLContainerBaseTest{

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate; // Para reiniciar el contador de IDs

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("ALTER TABLE users AUTO_INCREMENT = 1");
    }

    @Test
    void shouldCreateUser() throws Exception {

        mockMvc.perform(post(TestConstants.ENDPOINT_AUTH+"/sign-up")
                .contentType(APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userDTO)))
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
