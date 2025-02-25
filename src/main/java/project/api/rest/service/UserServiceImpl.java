package project.api.rest.service;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.api.rest.entity.User;
import project.api.rest.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findById(int id) {
        return Optional.ofNullable(userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + id + " no encontrado")));

    }

    @Override
    public User createUser(User user) {

        if (user.getId() != null && userRepository.existsById(user.getId())) {
            throw new IllegalArgumentException("User with ID  " + user.getId() + " already exists");
        }

        return userRepository.save(user);
    }

    @Override
    public User updateUser(User user) {

        if (user.getId() == null || !userRepository.existsById(user.getId())) {
            throw new EntityNotFoundException("Unable to update, user not found with ID: " + user.getId());
        }

        return userRepository.save(user);
    }

    @Override
    public void deleteUser(int id) {

        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User with ID " + id + " not found");
        }

        userRepository.deleteById(id);
    }
}
