package project.api.rest.service;


import org.springframework.security.core.userdetails.UserDetailsService;
import project.api.rest.entity.User;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface UserService extends UserDetailsService {

    List<User> findAllUsers(Pageable pageable);

    User findById(Integer id);

    User findByEmail(String email);

    User createUser(User user);

    User updateUser(Integer id, User user);

    void deleteUser(Integer id);
}

