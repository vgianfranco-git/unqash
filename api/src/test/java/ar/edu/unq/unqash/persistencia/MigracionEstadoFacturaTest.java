package ar.edu.unq.unqash.persistencia;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MigracionEstadoFacturaTest {

    @Test
    void inicializaLasFacturasExistentesYRestringeLosEstadosPersistidos() {
        SingleConnectionDataSource dataSource = new SingleConnectionDataSource(
                "jdbc:h2:mem:migracion_estado_" + UUID.randomUUID() + ";MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
                "sa", "", true);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.execute("CREATE TABLE UNQASH_FACTURA (C_ID INT PRIMARY KEY)");
        jdbcTemplate.update("INSERT INTO UNQASH_FACTURA (C_ID) VALUES (1)");

        Flyway.configure().dataSource(dataSource).baselineOnMigrate(true).baselineVersion("11").target("12")
                .load().migrate();

        assertThat(jdbcTemplate.queryForMap("SELECT * FROM UNQASH_FACTURA WHERE C_ID = 1"))
                .containsEntry("C_ESTADO", "ACTIVA");
        jdbcTemplate.update("INSERT INTO UNQASH_FACTURA (C_ID) VALUES (2)");
        assertThat(jdbcTemplate.queryForObject(
                "SELECT C_ESTADO FROM UNQASH_FACTURA WHERE C_ID = 2", String.class)).isEqualTo("ACTIVA");
        jdbcTemplate.update("UPDATE UNQASH_FACTURA SET C_ESTADO = 'ANULADA' WHERE C_ID = 1");
        assertThat(jdbcTemplate.queryForObject(
                "SELECT C_ESTADO FROM UNQASH_FACTURA WHERE C_ID = 1", String.class)).isEqualTo("ANULADA");
        assertThatThrownBy(() -> jdbcTemplate.update(
                "UPDATE UNQASH_FACTURA SET C_ESTADO = NULL WHERE C_ID = 1"))
                .isInstanceOf(DataIntegrityViolationException.class);
        assertThatThrownBy(() -> jdbcTemplate.update(
                "UPDATE UNQASH_FACTURA SET C_ESTADO = 'OTRO' WHERE C_ID = 1"))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
