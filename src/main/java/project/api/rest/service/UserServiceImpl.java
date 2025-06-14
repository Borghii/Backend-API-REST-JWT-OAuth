package project.api.rest.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import project.api.rest.entity.Role;
import project.api.rest.entity.User;
import project.api.rest.repository.RoleRepository;
import project.api.rest.repository.UserRepository;


import org.springframework.data.domain.Pageable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;


    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }




    @Override
    public List<User> findAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).getContent();
    }

    @Override
    public User findById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id: " + id + " not found"));
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User with email: " + email + " not found"));
    }

    @Override
    public User createUser(User user) {

        if (userExist(user)) {
            throw new IllegalArgumentException("User with email " + user.getEmail() + " already exists");
        }

        Set<Role> roles = getRoles(user);

        if (roles.isEmpty()) {
            throw new IllegalArgumentException("The roles specified does not exist.");
        }

        user.setRoles(roles);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    boolean userExist(User user) {
        return userRepository.existsByEmail(user.getEmail());
    }

    @Override
    public User updateUser(Integer id, User user) {


        User currentUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id: " + id + " not found"));

        if (!currentUser.getEmail().equals(user.getEmail()) && userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("User with email " + user.getEmail() + " already exists");
        }

        Set<Role> roles = getRoles(user);

        currentUser.setName(user.getName());
        currentUser.setSurname(user.getSurname());
        currentUser.setEmail(user.getEmail());
        currentUser.setPassword(passwordEncoder.encode(user.getPassword()));
        currentUser.setRoles(roles);


        return userRepository.save(currentUser);
    }

    HashSet<Role> getRoles(User user) {
        return new HashSet<>(roleRepository.findRoleByRoleEnumIn(user.getRoles().stream()
                .map(Role::getRoleEnum)
                .collect(Collectors.toList())));
    }


    @Override
    public void deleteUser(Integer id) {

        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User with id: " + id + " not found");
        }

        userRepository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with email: " + username + " not found"));

        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

        //adding roles
        user.getRoles().forEach(role -> authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRoleEnum().name()))));

        //adding roles' permissions
        user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getPermissionName())));

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorityList);
    }
}
