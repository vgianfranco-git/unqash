package ar.edu.unq.unqash.usuario;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.sql.DriverManager;

import static org.assertj.core.api.Assertions.assertThat;

class UsuarioVersionMigracionTest {

    @Test
    void laMigracionInicializaVersionParaUsuariosExistentesYSiguePermitiendoAltas() throws Exception {
        try (var connection = DriverManager.getConnection("jdbc:h2:mem:usuarios-version-migracion;MODE=PostgreSQL");
             var statement = connection.createStatement();
             var esquema = getClass().getResourceAsStream("/db/migration/V6__create_usuario.sql");
             var migracion = getClass().getResourceAsStream("/db/migration/V13__add_version_a_usuario.sql")) {
            assertThat(migracion).as("Migración para persistir la versión de usuarios").isNotNull();
            statement.execute(new String(esquema.readAllBytes(), StandardCharsets.UTF_8));
            statement.execute(alta("00000000-0000-0000-0000-000000000010", "40123450"));
            statement.execute(new String(migracion.readAllBytes(), StandardCharsets.UTF_8));
            statement.execute(alta("00000000-0000-0000-0000-000000000020", "40123451"));
            try (var filas = statement.executeQuery("SELECT N_VERSION FROM UNQASH_USUARIO ORDER BY N_DNI")) {
                assertThat(filas.next()).isTrue();
                assertThat(filas.getLong(1)).isZero();
                assertThat(filas.wasNull()).isFalse();
                assertThat(filas.next()).isTrue();
                assertThat(filas.getLong(1)).isZero();
                assertThat(filas.wasNull()).isFalse();
                assertThat(filas.next()).isFalse();
            }
        }
    }

    private String alta(String id, String dni) {
        return """
                INSERT INTO UNQASH_USUARIO
                  (C_ID, D_APELLIDO, D_NOMBRE, N_DNI, D_EMAIL, N_TELEFONO, D_CONTRASENA_HASH, FECHA_HORA_ALTA, USUARIO_ALTA)
                VALUES ('%s', 'Apellido', 'Usuario', '%s', '%s@test.co',
                  '%s', 'hash', TIMESTAMP '2026-10-02 10:00:00', 'ADMIN')
                """.formatted(id, dni, dni, dni);
    }
}
