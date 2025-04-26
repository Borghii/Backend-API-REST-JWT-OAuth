package project.api.rest.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import project.api.rest.constants.TestConstants;
import project.api.rest.entity.Role;
import project.api.rest.entity.RoleEnum;
import project.api.rest.entity.User;
import project.api.rest.repository.RoleRepository;
import project.api.rest.repository.UserRepository;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Captor
    private ArgumentCaptor<User> userCaptor;

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

    @Test
    void testGetRoles() {
        // given
        User user = TestConstants.USER;

        List<RoleEnum> roleEnums = List.of(RoleEnum.ADMIN);


        when(roleRepository.findRoleByRoleEnumIn(roleEnums)).thenReturn(List.of(new Role(RoleEnum.ADMIN)));

        // when
        HashSet<Role> result = userService.getRoles(user);

        // then
        assertThat(result.size()).isEqualTo(1);
        verify(roleRepository).findRoleByRoleEnumIn(anyList());
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
    class findByEmail {
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
    class createUser {
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
            User user = TestConstants.USER;

            UserServiceImpl spyService = Mockito.spy(userService);
            doReturn(false).when(spyService).userExist(user);
            doReturn(new HashSet<Role>()).when(spyService).getRoles(user);

            // when + then
            assertThatThrownBy(() -> spyService.createUser(user))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("The roles specified does not exist.");
        }

        @Test
        void shouldSetUserCorrectly() throws Exception {
            //given
            User user = TestConstants.USER;

            UserServiceImpl spyUserService = Mockito.spy(userService);
            doReturn(false).when(spyUserService).userExist(user);
            doReturn(new HashSet<Role>(List.of(new Role(RoleEnum.ADMIN)))).when(spyUserService).getRoles(user);

            //when
            spyUserService.createUser(user);

            //then
            verify(userRepository).save(userCaptor.capture());
            User savedUser = userCaptor.getValue();
            assertThat(savedUser.getRoles()).isEqualTo(user.getRoles());
            assertThat(savedUser.getPassword()).isEqualTo(passwordEncoder.encode(user.getPassword()));


        }


    }

    @Nested
    class updateUser {
        @Test
        void shouldThrowExceptionWhenUserNotFound() {
            // given
            Integer id = 1;
            User user = new User();
            when(userRepository.findById(id)).thenReturn(Optional.empty());

            // when + then
            assertThatThrownBy(() -> userService.updateUser(id, user))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("User with id: 1 not found");
        }

        @Test
        void shouldThrowExceptionWhenEmailAlreadyExists() {
            // given
            Integer id = 1;
            String newEmail = "new@example.com";

            User currentUser = new User();
            currentUser.setId(id);
            currentUser.setEmail("old@example.com");

            User updateData = new User();
            updateData.setEmail(newEmail);

            when(userRepository.findById(id)).thenReturn(Optional.of(currentUser));
            when(userRepository.existsByEmail(newEmail)).thenReturn(true);

            // when + then
            assertThatThrownBy(() -> userService.updateUser(id, updateData))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("User with email new@example.com already exists");
        }

        @Test
        void shouldUpdateUserCorrectly() {
            // given
            Integer id = 1;

            User currentUser = TestConstants.USER;

            User updateData = new User();
            updateData.setName("New Name");
            updateData.setSurname("New Surname");
            updateData.setEmail("new@example.com");
            Set<Role> newRoles = new HashSet<>();
            newRoles.add(new Role(RoleEnum.ADMIN));
            updateData.setRoles(newRoles);

            when(userRepository.findById(id)).thenReturn(Optional.of(currentUser));
            when(userRepository.existsByEmail("new@example.com")).thenReturn(false);

            UserServiceImpl spyUserService = Mockito.spy(userService);
            doReturn(newRoles).when(spyUserService).getRoles(updateData);
            when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

            // when
            User result = spyUserService.updateUser(id, updateData);

            // then
            verify(userRepository).save(userCaptor.capture());
            User savedUser = userCaptor.getValue();

            assertThat(savedUser.getName()).isEqualTo("New Name");
            assertThat(savedUser.getSurname()).isEqualTo("New Surname");
            assertThat(savedUser.getEmail()).isEqualTo("new@example.com");
            assertThat(savedUser.getRoles()).isEqualTo(newRoles);
        }
    }

    @Nested
    class deleteUser {
        @Test
        void shouldThrowExceptionWhenUserNotFound() {
            // given
            Integer id = 1;
            when(userRepository.existsById(id)).thenReturn(false);

            // when + then
            assertThatThrownBy(() -> userService.deleteUser(id))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("User with id: 1 not found");
        }

        @Test
        void shouldDeleteUserWhenExists() {
            // given
            Integer id = 1;
            when(userRepository.existsById(id)).thenReturn(true);

            // when
            userService.deleteUser(id);

            // then
            verify(userRepository).deleteById(id);
        }
    }

    @Nested
    class loadUserByUsername {
        @Test
        void shouldThrowExceptionWhenUserNotFound() {
            // given
            String email = "test@example.com";
            when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

            // when + then
            assertThatThrownBy(() -> userService.loadUserByUsername(email))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("User with email: test@example.com not found");
        }

        @Test
        void shouldReturnUserDetailsWithRolesAndPermissions() {
            // given

            User user = TestConstants.USER;

            when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

            // when
            UserDetails userDetails = userService.loadUserByUsername(user.getEmail());

            // then
            assertThat(userDetails).isNotNull();
            assertThat(userDetails.getUsername()).isEqualTo(user.getEmail());
            assertThat(userDetails.getPassword()).isEqualTo(user.getPassword());

            Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
            assertThat(authorities.size()).isEqualTo(5);
        }
    }
}