package project.api.rest.service;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.api.rest.dto.UserDTO;
import project.api.rest.entity.User;
import project.api.rest.repository.UserRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
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
    public User findById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id: " + id + " not found"));
    }


    @Override
    public User createUser(User user) {

        if (userExist(user)) {
            throw new IllegalArgumentException("User with email " + user.getEmail() + " already exists");
        }

        return userRepository.save(user);
    }

    private boolean userExist(User user){
        return userRepository.existsByEmail(user.getEmail());
    }

    @Override
    public User updateUser(Integer id, User user) {


        User currentUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id: " + id + " not found"));

        if (!currentUser.getEmail().equals(user.getEmail()) && userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("User with email " + user.getEmail() + " already exists");
        }

        currentUser.setName(user.getName());
        currentUser.setSurname(user.getSurname());
        currentUser.setEmail(user.getEmail());


        return userRepository.save(currentUser);
    }




    @Override
    public void deleteUser(Integer id) {

        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User with id: " + id + " not found");
        }

        userRepository.deleteById(id);
    }
}
