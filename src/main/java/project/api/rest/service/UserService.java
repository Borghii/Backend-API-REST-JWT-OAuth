package project.api.rest.service;


import project.api.rest.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> findAllUsers();

    Optional<User> findById(int id);

    User createUser(User user);

    User updateUser(User user);

    void deleteUser(int id);
}

