package project.api.rest.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import project.api.rest.ContainerDB.MySQLContainerBaseIntTest;
import project.api.rest.entity.Permission;
import project.api.rest.entity.Role;
import project.api.rest.entity.RoleEnum;
import project.api.rest.repository.RoleRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static project.api.rest.entity.RoleEnum.*;

@SpringBootTest
@ActiveProfiles(value = "test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Importante: evita que Spring Boot use H2
class RoleRepositoryTest extends MySQLContainerBaseIntTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void shouldFindRoleByRoleEnumIn() {

        //GIVEN
        List<RoleEnum> roleNames = List.of(ADMIN,DEVELOPER,INVITED);

        //WHEN
        List<Role> roles = roleRepository.findRoleByRoleEnumIn(roleNames);


        //THEN
        assertThat(roles.size()).isEqualTo(3);
        assertThat(roles.stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getPermissionName)
                .collect(Collectors.toSet()).size()).isEqualTo(4);


    }
}