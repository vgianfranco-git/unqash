package ar.edu.unq.unqash.usuario;

import ar.edu.unq.unqash.persistencia.UsuarioEntity;
import ar.edu.unq.unqash.persistencia.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.regex.Matcher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:usuarios-edicion;MODE=PostgreSQL;DB_CLOSE_DELAY=-1"
})
class UsuarioEdicionControllerTest {

    private static final UUID GESTOR_ID = UUID.fromString("00000000-0000-0000-0000-000000000010");
    private static final UUID USUARIO_ID = UUID.fromString("00000000-0000-0000-0000-000000000020");
    private static final LocalDateTime FECHA_ALTA = LocalDateTime.of(2026, 10, 2, 10, 0);
    private static final String HASH_ORIGINAL = new BCryptPasswordEncoder().encode("OriginalSegura1!");

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();
        usuarioRepository.save(new UsuarioEntity(GESTOR_ID, "Gestor", "Admin", "40123450",
                "gestor@example.test", "1140123450", "hash-gestor", FECHA_ALTA, "SUPER_ADMIN", true));
        usuarioRepository.save(new UsuarioEntity(USUARIO_ID, "Original", "Usuario", "40123451",
                "usuario@example.test", "1140123451", HASH_ORIGINAL, FECHA_ALTA, "ADMIN_ALTA", false));
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void editaLosDatosPublicosYConservaIdentidadAuditoriaYHashSinContrasena() throws Exception {
        mockMvc.perform(put("/usuarios/{idUsuario}", USUARIO_ID).session(sesion(GESTOR_ID.toString()))
                        .contentType(MediaType.APPLICATION_JSON).content(datosValidos()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USUARIO_ID.toString()))
                .andExpect(jsonPath("$.apellido").value("Actualizado"))
                .andExpect(jsonPath("$.nombre").value("Usuario Nuevo"))
                .andExpect(jsonPath("$.dni").value("40234567"))
                .andExpect(jsonPath("$.email").value("nuevo@example.test"))
                .andExpect(jsonPath("$.telefono").value("1198765432"))
                .andExpect(jsonPath("$.esGestor").value(true))
                .andExpect(jsonPath("$.fechaHoraAlta").value("2026-10-02T10:00:00"))
                .andExpect(jsonPath("$.usuarioDeAlta").value("ADMIN_ALTA"))
                .andExpect(jsonPath("$.contrasena").doesNotExist())
                .andExpect(jsonPath("$.contrasenaHash").doesNotExist());

        UsuarioEntity guardado = usuarioRepository.findById(USUARIO_ID).orElseThrow();
        assertThat(guardado.getApellido()).isEqualTo("Actualizado");
        assertThat(guardado.getNombre()).isEqualTo("Usuario Nuevo");
        assertThat(guardado.getDni()).isEqualTo("40234567");
        assertThat(guardado.getEmail()).isEqualTo("nuevo@example.test");
        assertThat(guardado.getTelefono()).isEqualTo("1198765432");
        assertThat(guardado.getEsGestor()).isTrue();
        assertThat(guardado.getFechaHoraAlta()).isEqualTo(FECHA_ALTA);
        assertThat(guardado.getUsuarioAlta()).isEqualTo("ADMIN_ALTA");
        assertThat(guardado.getContrasenaHash()).isEqualTo(HASH_ORIGINAL);
        assertThat(usuarioRepository.count()).isEqualTo(2);
    }

    @Test
    void rechazaEditarSinSesionSinModificarElUsuario() throws Exception {
        mockMvc.perform(put("/usuarios/{idUsuario}", USUARIO_ID)
                        .contentType(MediaType.APPLICATION_JSON).content(datosValidos()))
                .andExpect(status().isUnauthorized());
        assertUsuarioSinCambios();
    }

    @Test
    void rechazaEditarSinRolGestorSinModificarElUsuario() throws Exception {
        mockMvc.perform(put("/usuarios/{idUsuario}", USUARIO_ID).session(sesion(USUARIO_ID.toString()))
                        .contentType(MediaType.APPLICATION_JSON).content(datosValidos()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Usuario sin permisos"));
        assertUsuarioSinCambios();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "id-invalido", "00000000-0000-0000-0000-000000000099"})
    void rechazaUnaSesionSinUsuarioValido(String idSesion) throws Exception {
        mockMvc.perform(put("/usuarios/{idUsuario}", USUARIO_ID).session(sesion(idSesion))
                        .contentType(MediaType.APPLICATION_JSON).content(datosValidos()))
                .andExpect(status().isUnauthorized());
        assertUsuarioSinCambios();
    }

    private void assertUsuarioSinCambios() {
        UsuarioEntity usuario = usuarioRepository.findById(USUARIO_ID).orElseThrow();
        assertThat(usuario.getApellido()).isEqualTo("Original");
        assertThat(usuario.getNombre()).isEqualTo("Usuario");
        assertThat(usuario.getDni()).isEqualTo("40123451");
        assertThat(usuario.getEmail()).isEqualTo("usuario@example.test");
        assertThat(usuario.getTelefono()).isEqualTo("1140123451");
        assertThat(usuario.getEsGestor()).isFalse();
        assertThat(usuario.getContrasenaHash()).isEqualTo(HASH_ORIGINAL);
    }

    @Test
    void rechazaUnUsuarioInexistente() throws Exception {
        mockMvc.perform(put("/usuarios/00000000-0000-0000-0000-000000000099")
                        .session(sesion(GESTOR_ID.toString()))
                        .contentType(MediaType.APPLICATION_JSON).content(datosValidos()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Usuario inexistente."));
        assertThat(usuarioRepository.count()).isEqualTo(2);
        assertUsuarioSinCambios();
    }

    @ParameterizedTest
    @CsvSource({
            "apellido, Apellido123", "nombre, Nombre123", "dni, abc",
            "email, invalido", "telefono, abc", "apellido, ''", "nombre, ''",
            "dni, ''", "email, ''", "telefono, ''"
    })
    void rechazaFormatosInvalidosOCamposVaciosSinModificarElUsuario(String campo, String valor) throws Exception {
        mockMvc.perform(put("/usuarios/{idUsuario}", USUARIO_ID).session(sesion(GESTOR_ID.toString()))
                        .contentType(MediaType.APPLICATION_JSON).content(datosCon(campo, valor)))
                .andExpect(status().isBadRequest());
        assertUsuarioSinCambios();
    }

    private String datosCon(String campo, String valor) {
        return datosValidos().replaceFirst("\"" + campo + "\":\"[^\"]*\"",
                Matcher.quoteReplacement("\"" + campo + "\":\"" + valor + "\""));
    }

    @ParameterizedTest
    @CsvSource({
            "dni, 40123450, DNI ya registrado",
            "email, gestor@example.test, email ya registrado",
            "telefono, 1140123450, teléfono ya registrado"
    })
    void rechazaUnDatoDeOtroUsuarioSinModificarElUsuario(String campo, String valor, String detalle) {
        assertThatCode(() -> mockMvc.perform(put("/usuarios/{idUsuario}", USUARIO_ID)
                        .session(sesion(GESTOR_ID.toString()))
                        .contentType(MediaType.APPLICATION_JSON).content(datosCon(campo, valor)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value(detalle))).doesNotThrowAnyException();
        assertUsuarioSinCambios();
    }

    @Test
    void aceptaLosDatosUnicosDelPropioUsuario() throws Exception {
        String datos = datosValidos().replace("40234567", "40123451")
                .replace("nuevo@example.test", "usuario@example.test")
                .replace("1198765432", "1140123451");
        mockMvc.perform(put("/usuarios/{idUsuario}", USUARIO_ID).session(sesion(GESTOR_ID.toString()))
                        .contentType(MediaType.APPLICATION_JSON).content(datos))
                .andExpect(status().isOk());
        assertThat(usuarioRepository.findById(USUARIO_ID).orElseThrow().getDni()).isEqualTo("40123451");
    }

    @ParameterizedTest
    @CsvSource({"apellido, 51", "nombre, 51", "email, 51"})
    void rechazaCamposQueExcedenElMaximoSinModificarElUsuario(String campo, int longitud) {
        String valor = campo.equals("email") ? "a".repeat(longitud - 8) + "@test.co" : "A".repeat(longitud);
        assertThatCode(() -> mockMvc.perform(put("/usuarios/{idUsuario}", USUARIO_ID)
                        .session(sesion(GESTOR_ID.toString()))
                        .contentType(MediaType.APPLICATION_JSON).content(datosCon(campo, valor)))
                .andExpect(status().isBadRequest())).doesNotThrowAnyException();
        assertUsuarioSinCambios();
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 30})
    void reemplazaLaContrasenaPorUnHashBCryptSinExponerCredenciales(int longitud) throws Exception {
        String contrasena = "A" + "a".repeat(longitud - 2) + "!";
        mockMvc.perform(put("/usuarios/{idUsuario}", USUARIO_ID).session(sesion(GESTOR_ID.toString()))
                        .contentType(MediaType.APPLICATION_JSON).content(conContrasena(contrasena)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contrasena").doesNotExist())
                .andExpect(jsonPath("$.contrasenaHash").doesNotExist());
        UsuarioEntity guardado = usuarioRepository.findById(USUARIO_ID).orElseThrow();
        assertThat(guardado.getContrasenaHash()).isNotEqualTo(HASH_ORIGINAL).isNotEqualTo(contrasena);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        assertThat(encoder.matches(contrasena, guardado.getContrasenaHash())).isTrue();
        assertThat(encoder.matches("OriginalSegura1!", guardado.getContrasenaHash())).isFalse();
        assertThat(guardado.getFechaHoraAlta()).isEqualTo(FECHA_ALTA);
        assertThat(guardado.getUsuarioAlta()).isEqualTo("ADMIN_ALTA");
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {""})
    void conservaElHashSiLaContrasenaEsNullOVacia(String contrasena) throws Exception {
        mockMvc.perform(put("/usuarios/{idUsuario}", USUARIO_ID).session(sesion(GESTOR_ID.toString()))
                        .contentType(MediaType.APPLICATION_JSON).content(conContrasena(contrasena)))
                .andExpect(status().isOk());
        assertThat(usuarioRepository.findById(USUARIO_ID).orElseThrow().getContrasenaHash())
                .isEqualTo(HASH_ORIGINAL);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Corta1!", "segurasinmayuscula!", "SeguraSinSimbolo",
            "Aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa!", "          "})
    void rechazaUnaContrasenaNuevaInvalidaSinModificarElUsuario(String contrasena) throws Exception {
        mockMvc.perform(put("/usuarios/{idUsuario}", USUARIO_ID).session(sesion(GESTOR_ID.toString()))
                        .contentType(MediaType.APPLICATION_JSON).content(conContrasena(contrasena)))
                .andExpect(status().isBadRequest());
        assertUsuarioSinCambios();
    }

    @ParameterizedTest
    @ValueSource(strings = {"ausente", "null"})
    void conservaElRolSiEsGestorNoEstaDefinido(String valor) {
        String datos = valor.equals("ausente") ? datosValidos().replace(",\"esGestor\":true", "")
                : datosValidos().replace("\"esGestor\":true", "\"esGestor\":null");
        assertThatCode(() -> mockMvc.perform(put("/usuarios/{idUsuario}", GESTOR_ID)
                        .session(sesion(GESTOR_ID.toString()))
                        .contentType(MediaType.APPLICATION_JSON).content(datos))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.esGestor").value(true))).doesNotThrowAnyException();
        assertThat(usuarioRepository.findById(GESTOR_ID).orElseThrow().getEsGestor()).isTrue();
    }

    @Test
    void permiteRevocarElRolGestorExplicitamente() throws Exception {
        mockMvc.perform(put("/usuarios/{idUsuario}", GESTOR_ID).session(sesion(GESTOR_ID.toString()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(datosValidos().replace("\"esGestor\":true", "\"esGestor\":false")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.esGestor").value(false));
        assertThat(usuarioRepository.findById(GESTOR_ID).orElseThrow().getEsGestor()).isFalse();
    }

    private String conContrasena(String contrasena) {
        String valorJson = contrasena == null ? "null" : "\"" + contrasena + "\"";
        return datosValidos().replace("}", ",\"contrasena\":" + valorJson + "}");
    }

    private String datosValidos() {
        return """
                {"apellido":" Actualizado ","nombre":" Usuario Nuevo ","dni":"40234567",
                 "email":"nuevo@example.test","telefono":"1198765432","esGestor":true}
                """;
    }

    private MockHttpSession sesion(String id) {
        MockHttpSession sesion = new MockHttpSession();
        if (!id.isEmpty()) {
            sesion.setAttribute("usuarioId", id);
        }
        return sesion;
    }
}
