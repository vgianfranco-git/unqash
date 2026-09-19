package ar.edu.unq.unqash.usuario;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ar.edu.unq.unqash.persistencia.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:usuarios;MODE=PostgreSQL;DB_CLOSE_DELAY=-1"
})
class UsuarioControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void creaUnUsuarioConDatosValidos() throws Exception {
        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "apellido":"Prueba",
                                  "nombre":"Usuario Demo",
                                  "dni":"40123456",
                                  "email":"usuario.prueba@example.test",
                                  "telefono":"1112345678",
                                  "contrasena":"PruebaSegura1!"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.apellido").value("Prueba"))
                .andExpect(jsonPath("$.nombre").value("Usuario Demo"))
                .andExpect(jsonPath("$.dni").value("40123456"))
                .andExpect(jsonPath("$.email").value("usuario.prueba@example.test"))
                .andExpect(jsonPath("$.telefono").value("1112345678"))
                .andExpect(jsonPath("$.usuarioDeAlta").value("SUPER_ADMIN"));
    }

    @Test
    void rechazaUnDniConFormatoInvalido() throws Exception {
        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "apellido":"Prueba",
                                  "nombre":"Usuario Demo",
                                  "dni":"abc",
                                  "email":"otro.usuario@example.test",
                                  "telefono":"1198765432",
                                  "contrasena":"PruebaSegura1!"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("DNI debe contener entre 7 y 8 dígitos"));
    }

    @Test
    void rechazaUnDniQueYaFueRegistrado() throws Exception {
        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "apellido":"Primero",
                                  "nombre":"Usuario Demo",
                                  "dni":"40234567",
                                  "email":"primer.usuario@example.test",
                                  "telefono":"1111111111",
                                  "contrasena":"PruebaSegura1!"
                                }
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "apellido":"Segundo",
                                  "nombre":"Usuario Demo",
                                  "dni":"40234567",
                                  "email":"segundo.usuario@example.test",
                                  "telefono":"1222222222",
                                  "contrasena":"PruebaSegura1!"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("DNI ya registrado"));
    }

    @Test
    void guardaLaContrasenaHasheadaSinExponerlaEnLaRespuesta() throws Exception {
        String contrasena = "PruebaSegura1!";

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "apellido":"Hash",
                                  "nombre":"Usuario Demo",
                                  "dni":"40345678",
                                  "email":"usuario.hash@example.test",
                                  "telefono":"1333333333",
                                  "contrasena":"%s"
                                }
                                """.formatted(contrasena)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.contrasena").doesNotExist())
                .andExpect(jsonPath("$.contrasenaHash").doesNotExist());

        String hashGuardado = usuarioRepository.findAll().stream()
                .filter(usuario -> usuario.getDni().equals("40345678"))
                .findFirst()
                .orElseThrow()
                .getContrasenaHash();

        assertThat(hashGuardado).isNotEqualTo(contrasena);
        assertThat(new BCryptPasswordEncoder().matches(contrasena, hashGuardado)).isTrue();
    }
}
