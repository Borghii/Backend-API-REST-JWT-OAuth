package project.api.rest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.api.rest.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByEmail(String email);
}
