package ar.edu.unq.unqash.auth;

import ar.edu.unq.unqash.persistencia.UsuarioEntity;
import ar.edu.unq.unqash.persistencia.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:auth;MODE=PostgreSQL;DB_CLOSE_DELAY=-1"
})
class AuthControllerTest {

    private static final UUID USUARIO_ID = UUID.fromString("00000000-0000-0000-0000-000000000010");
    private static final String DNI = "40123456";
    private static final String CONTRASENA = "PruebaSegura1!";

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();
        usuarioRepository.save(new UsuarioEntity(
                USUARIO_ID,
                "Prueba",
                "Usuario",
                DNI,
                "usuario.auth@example.test",
                "1112345678",
                new BCryptPasswordEncoder().encode(CONTRASENA),
                LocalDateTime.of(2026, 9, 19, 10, 0),
                "SUPER_ADMIN"
        ));
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void iniciaSesionYPermiteRecuperarElUsuarioAutenticado() throws Exception {
        MvcResult resultadoLogin = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"dni":"40123456","contrasena":"PruebaSegura1!"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USUARIO_ID.toString()))
                .andExpect(jsonPath("$.dni").value(DNI))
                .andExpect(jsonPath("$.contrasena").doesNotExist())
                .andExpect(jsonPath("$.contrasenaHash").doesNotExist())
                .andReturn();

        MockHttpSession sesion = (MockHttpSession) resultadoLogin.getRequest().getSession(false);

        mockMvc.perform(get("/auth/sesion").session(sesion))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USUARIO_ID.toString()))
                .andExpect(jsonPath("$.nombre").value("Usuario"));
    }

    @Test
    void rechazaCredencialesInvalidasSinRevelarSiElDniExiste() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"dni":"40123456","contrasena":"ClaveIncorrecta1!"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.detail").value("DNI o contraseña incorrectos"));
    }

    @Test
    void rechazaUnDniInexistenteConElMismoErrorGenerico() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"dni":"40987654","contrasena":"PruebaSegura1!"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.detail").value("DNI o contraseña incorrectos"));
    }

    @Test
    void rechazaUnaContrasenaFueraDelRangoPermitido() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"dni":"40123456","contrasena":"Corta1!"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("contraseña debe tener entre 10 y 30 caracteres"));
    }

    @Test
    void rechazaRecuperarLaSesionCuandoNoHayUnaActiva() throws Exception {
        mockMvc.perform(get("/auth/sesion"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.detail").value("No hay una sesión activa"));
    }
}
