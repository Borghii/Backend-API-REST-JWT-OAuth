package project.api.rest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.api.rest.entity.Role;
import project.api.rest.entity.RoleEnum;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    //SELECT * FROM role WHERE role_enum IN ('ADMIN', 'USER', 'DEVELOPER');
    List<Role> findRoleByRoleEnumIn(List<RoleEnum> roleNames);
}
