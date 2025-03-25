package project.api.rest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.api.rest.entity.Role;
import project.api.rest.entity.RoleEnum;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    List<Role> findRoleByRoleEnumIn(List<String> roleNames);
}
