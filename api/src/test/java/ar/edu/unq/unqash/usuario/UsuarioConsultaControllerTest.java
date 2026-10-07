package ar.edu.unq.unqash.usuario;

import ar.edu.unq.unqash.persistencia.UsuarioEntity;
import ar.edu.unq.unqash.persistencia.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:usuarios-consulta;MODE=PostgreSQL;DB_CLOSE_DELAY=-1"
})
class UsuarioConsultaControllerTest {

    private static final UUID GESTOR_ID = UUID.fromString("00000000-0000-0000-0000-000000000010");
    private static final UUID USUARIO_ID = UUID.fromString("00000000-0000-0000-0000-000000000060");
    private static final UUID EMPATE_MENOR_ID = UUID.fromString("00000000-0000-0000-0000-000000000030");
    private static final UUID EMPATE_MAYOR_ID = UUID.fromString("00000000-0000-0000-0000-000000000040");
    private static final UUID RECIENTE_ID = UUID.fromString("00000000-0000-0000-0000-000000000050");

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();
        // Insertar el empate menor antes del mayor evita depender del orden de inserción.
        guardarUsuario(GESTOR_ID, "40123450", LocalDateTime.of(2026, 10, 1, 10, 0), true);
        guardarUsuario(USUARIO_ID, "40123451", LocalDateTime.of(2026, 10, 2, 10, 0), false);
        guardarUsuario(EMPATE_MENOR_ID, "40123452", LocalDateTime.of(2026, 10, 3, 10, 0), false);
        guardarUsuario(EMPATE_MAYOR_ID, "40123453", LocalDateTime.of(2026, 10, 3, 10, 0), false);
        guardarUsuario(RECIENTE_ID, "40123454", LocalDateTime.of(2026, 10, 4, 10, 0), false);
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void historialUsaPaginaUnoYTresUsuariosPorDefectoConOrdenEstable() throws Exception {
        mockMvc.perform(get("/usuarios/historial").session(sesion(GESTOR_ID.toString())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuarios.length()").value(3))
                .andExpect(jsonPath("$.usuarios[0].id").value(RECIENTE_ID.toString()))
                .andExpect(jsonPath("$.usuarios[1].id").value(EMPATE_MAYOR_ID.toString()))
                .andExpect(jsonPath("$.usuarios[2].id").value(EMPATE_MENOR_ID.toString()))
                .andExpect(jsonPath("$.usuarios[0].email").value("40123454@example.test"))
                .andExpect(jsonPath("$.usuarios[0].telefono").value("1140123454"))
                .andExpect(jsonPath("$.numPag").value(1))
                .andExpect(jsonPath("$.totalPag").value(2))
                .andExpect(jsonPath("$.totalRegistros").value(5))
                .andExpect(jsonPath("$.usuarios[*].contrasena").isEmpty())
                .andExpect(jsonPath("$.usuarios[*].contrasenaHash").isEmpty());
    }

    @Test
    void historialRespetaPaginaYTamanioSolicitadosSinRepetirElEmpate() throws Exception {
        mockMvc.perform(get("/usuarios/historial").param("page", "2").param("size", "2")
                        .session(sesion(GESTOR_ID.toString())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuarios.length()").value(2))
                .andExpect(jsonPath("$.usuarios[0].id").value(EMPATE_MENOR_ID.toString()))
                .andExpect(jsonPath("$.usuarios[1].id").value(USUARIO_ID.toString()))
                .andExpect(jsonPath("$.numPag").value(2))
                .andExpect(jsonPath("$.totalPag").value(3))
                .andExpect(jsonPath("$.totalRegistros").value(5));
    }

    @Test
    void historialDevuelveLaUltimaPaginaIncompleta() throws Exception {
        mockMvc.perform(get("/usuarios/historial").param("page", "3").param("size", "2")
                        .session(sesion(GESTOR_ID.toString())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuarios.length()").value(1))
                .andExpect(jsonPath("$.usuarios[0].id").value(GESTOR_ID.toString()))
                .andExpect(jsonPath("$.numPag").value(3))
                .andExpect(jsonPath("$.totalPag").value(3))
                .andExpect(jsonPath("$.totalRegistros").value(5));
    }

    @Test
    void historialDevuelvePaginaVaciaSiExcedeElTotal() throws Exception {
        mockMvc.perform(get("/usuarios/historial").param("page", "4").param("size", "2")
                        .session(sesion(GESTOR_ID.toString())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuarios").isEmpty())
                .andExpect(jsonPath("$.numPag").value(4))
                .andExpect(jsonPath("$.totalPag").value(3))
                .andExpect(jsonPath("$.totalRegistros").value(5));
    }

    @ParameterizedTest
    @CsvSource({"0,3", "-1,3", "1,0", "1,-1"})
    void historialRechazaPaginaOTamanioNoPositivos(String page, String size) throws Exception {
        mockMvc.perform(get("/usuarios/historial").param("page", page).param("size", size)
                        .session(sesion(GESTOR_ID.toString())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void detalleDevuelveLosDatosPublicosDelUsuarioSolicitado() throws Exception {
        mockMvc.perform(get("/usuarios/{idUsuario}", USUARIO_ID).session(sesion(GESTOR_ID.toString())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USUARIO_ID.toString()))
                .andExpect(jsonPath("$.apellido").value("Apellido"))
                .andExpect(jsonPath("$.nombre").value("Usuario"))
                .andExpect(jsonPath("$.dni").value("40123451"))
                .andExpect(jsonPath("$.email").value("40123451@example.test"))
                .andExpect(jsonPath("$.telefono").value("1140123451"))
                .andExpect(jsonPath("$.esGestor").value(false))
                .andExpect(jsonPath("$.fechaHoraAlta").value("2026-10-02T10:00:00"))
                .andExpect(jsonPath("$.usuarioDeAlta").value("SUPER_ADMIN"))
                .andExpect(jsonPath("$.contrasena").doesNotExist())
                .andExpect(jsonPath("$.contrasenaHash").doesNotExist());
    }

    @Test
    void detalleRechazaUsuarioInexistenteConElErrorDeValidacionActual() throws Exception {
        mockMvc.perform(get("/usuarios/00000000-0000-0000-0000-000000000099")
                        .session(sesion(GESTOR_ID.toString())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Usuario inexistente."));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/usuarios/historial", "/usuarios/00000000-0000-0000-0000-000000000060"})
    void consultaRechazaLaAusenciaDeSesion(String ruta) throws Exception {
        mockMvc.perform(get(ruta))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.detail").value("No hay una sesión activa"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/usuarios/historial", "/usuarios/00000000-0000-0000-0000-000000000060"})
    void consultaRechazaUnUsuarioSinRolGestor(String ruta) throws Exception {
        mockMvc.perform(get(ruta).session(sesion(USUARIO_ID.toString())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Usuario sin permisos"));
    }

    @ParameterizedTest
    @CsvSource({
            "/usuarios/historial, ''",
            "/usuarios/historial, id-invalido",
            "/usuarios/historial, 00000000-0000-0000-0000-000000000099",
            "/usuarios/00000000-0000-0000-0000-000000000060, ''",
            "/usuarios/00000000-0000-0000-0000-000000000060, id-invalido",
            "/usuarios/00000000-0000-0000-0000-000000000060, 00000000-0000-0000-0000-000000000099"
    })
    void consultaRechazaUnaSesionSinUsuarioValido(String ruta, String idSesion) throws Exception {
        mockMvc.perform(get(ruta).session(sesion(idSesion)))
                .andExpect(status().isUnauthorized());
    }

    private void guardarUsuario(UUID id, String dni, LocalDateTime fechaHoraAlta, boolean esGestor) {
        usuarioRepository.save(new UsuarioEntity(id, "Apellido", "Usuario", dni, dni + "@example.test",
                "11" + dni, "hash-privado", fechaHoraAlta, "SUPER_ADMIN", esGestor));
    }

    private MockHttpSession sesion(String id) {
        MockHttpSession sesion = new MockHttpSession();
        if (!id.isEmpty()) {
            sesion.setAttribute("usuarioId", id);
        }
        return sesion;
    }
}
