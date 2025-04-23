package project.api.rest.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import project.api.rest.entity.User;
import project.api.rest.repository.RoleRepository;
import project.api.rest.repository.UserRepository;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void shouldCallFindByAll() {
        //when
        userService.findAllUsers();
        //then
        verify(userRepository).findAll();

        verify(userRepository, times(1)).findAll();

    }


    @Nested
    class findById {
        @Test
        void shouldReturnUserWhenExists() {
            // given
            int id = 1;
            User user = new User();
            user.setId(id);
            when(userRepository.findById(id)).thenReturn(Optional.of(user));

            // when
            User result = userService.findById(id);

            // then
            assertThat(result).isNotNull();
            assertThat(id).isEqualTo(user.getId());
        }


        @Test
        void shouldThrowExceptionWhenUserNotFound() {
            // given
            int id = 2;
            when(userRepository.findById(id)).thenReturn(Optional.empty());

            // when + then
            assertThatThrownBy(() -> userService.findById(id))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("User with id: 2 not found");
        }







    }

    @Nested
    class findByEmail{
        @Test
        void shouldThrowExceptionWhenUserWithEmailNotFound() {
            // given
            String email = "test@gmail.com";
            when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.empty());

            // when + then
            assertThatThrownBy(() -> userService.findByEmail("test@gmail.com"))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("User with email: test@gmail.com not found");
        }
    }

    @Nested
    class createUser{
        @Test
        void shouldThrowExceptionIfUserExists() {
            // given
            User user = new User();
            user.setEmail("test@example.com");

            // mock userExist()
            UserServiceImpl spyService = Mockito.spy(userService);
            doReturn(true).when(spyService).userExist(user);

            // when + then
            assertThatThrownBy(() -> spyService.createUser(user))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("User with email test@example.com already exists");
        }

        @Test
        void shouldThrowExceptionIfRolesAreEmpty() {
            // given
            User user = new User();
            user.setEmail("test@example.com");

            UserServiceImpl spyService = Mockito.spy(userService);
            doReturn(false).when(spyService).userExist(user);
            doReturn(Collections.emptySet()).when(spyService).getRoles(user);

            // when + then
            assertThatThrownBy(() -> spyService.createUser(user))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("The roles specified does not exist.");
        }



    }





    @Test
    void createUser() {
    }

    @Test
    void updateUser() {
    }

    @Test
    void deleteUser() {
    }

    @Test
    void loadUserByUsername() {
    }
}