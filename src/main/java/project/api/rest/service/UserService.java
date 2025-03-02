package project.api.rest.service;


import project.api.rest.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> findAllUsers();

    User findById(int id);

    User createUser(User user);

    User updateUser(int id, User user);

    void deleteUser(int id);
}

