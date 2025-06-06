package project.api.rest.ContainerDB;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MySQLContainer;

@ActiveProfiles(value = "test")
public class MySQLContainerBaseIntTest {

//    Si comparas esta clase con MySQLContainerTest, verás que las anotaciones
//    @Testcontainers y @Container han desaparecido. Si las dejamos, Testcontainers
//    seguirá creando un contenedor para cada clase hija, justo lo que pretendemos evitar.
//    Ahora la creación del contenedor la haremos a mano una única vez en un bloque static (línea 12).
//    De la destrucción del contenedor se responsabiliza nuestro colega Ryuk.

    @ServiceConnection
    protected static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.4.2")
            .withDatabaseName("testdb")
            .withUsername("root")
            .withPassword("password");

    static {
        mysqlContainer.start();
    }

}
