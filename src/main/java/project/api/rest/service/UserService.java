package project.api.rest.service;


import org.springframework.security.core.userdetails.UserDetailsService;
import project.api.rest.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {

    List<User> findAllUsers();

    User findById(Integer id);

    User createUser(User user);

    User updateUser(Integer id, User user);

    void deleteUser(Integer id);
}

