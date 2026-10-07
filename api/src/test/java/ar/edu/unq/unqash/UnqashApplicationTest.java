package ar.edu.unq.unqash;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:unqash_context;MODE=PostgreSQL;DB_CLOSE_DELAY=-1"
})
class UnqashApplicationTest {

    @Test
    void iniciaElContextoDeLaAplicacion() {
    }
}
