package ar.edu.unq.unqash.venta;

import ar.edu.unq.unqash.persistencia.TipoCobro;
import ar.edu.unq.unqash.persistencia.TipoCobroRepository;
import ar.edu.unq.unqash.persistencia.UsuarioEntity;
import ar.edu.unq.unqash.persistencia.UsuarioRepository;
import ar.edu.unq.unqash.persistencia.VentaRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:venta_usuario;MODE=PostgreSQL;DB_CLOSE_DELAY=-1"
})
class VentaConUsuarioControllerTest {

    private static final UUID TIPO_COBRO_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID USUARIO_A_ID = UUID.fromString("00000000-0000-0000-0000-000000000010");
    private static final UUID USUARIO_B_ID = UUID.fromString("00000000-0000-0000-0000-000000000020");
    private static final String CONTRASENA_A = "ClavePruebaA1!";
    private static final String CONTRASENA_B = "ClavePruebaB1!";

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TipoCobroRepository tipoCobroRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        ventaRepository.deleteAll();
        usuarioRepository.deleteAll();
        tipoCobroRepository.deleteAll();

        tipoCobroRepository.save(new TipoCobro(TIPO_COBRO_ID, "Efectivo"));
        usuarioRepository.save(usuario(USUARIO_A_ID, "40123456", "Ana", "Vendedora", CONTRASENA_A));
        usuarioRepository.save(usuario(USUARIO_B_ID, "40987654", "Bruno", "Vendedor", CONTRASENA_B));
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void registraLaVentaConElUsuarioDeLaSesionYRechazaUnaVentaSinSesion() throws Exception {
        MockHttpSession sesionA = iniciarSesion("40123456", CONTRASENA_A);

        mockMvc.perform(post("/ventas")
                        .session(sesionA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ventaJson("Venta de Ana")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.usuario.id").value(USUARIO_A_ID.toString()))
                .andExpect(jsonPath("$.usuario.nombre").value("Ana"))
                .andExpect(jsonPath("$.usuario.apellido").value("Vendedora"));

        mockMvc.perform(post("/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ventaJson("Venta sin sesión")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void cadaUsuarioSoloConsultaLasVentasQueRegistro() throws Exception {
        MockHttpSession sesionA = iniciarSesion("40123456", CONTRASENA_A);
        MockHttpSession sesionB = iniciarSesion("40987654", CONTRASENA_B);

        registrarVenta(sesionA, "Venta de Ana");
        registrarVenta(sesionB, "Venta de Bruno");

        mockMvc.perform(get("/ventas").session(sesionA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ventas.length()").value(1))
                .andExpect(jsonPath("$.ventas[0].producto").value("Venta de Ana"))
                .andExpect(jsonPath("$.ventas[0].usuario.id").value(USUARIO_A_ID.toString()));

        mockMvc.perform(get("/ventas").session(sesionB))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ventas.length()").value(1))
                .andExpect(jsonPath("$.ventas[0].producto").value("Venta de Bruno"))
                .andExpect(jsonPath("$.ventas[0].usuario.id").value(USUARIO_B_ID.toString()));
    }

    @Test
    void laAnulacionConservaElUsuarioDeLaVentaOriginal() throws Exception {
        MockHttpSession sesionA = iniciarSesion("40123456", CONTRASENA_A);
        registrarVenta(sesionA, "Venta anulable");
        UUID ventaId = ventaRepository.findAll().getFirst().getId();

        mockMvc.perform(post("/ventas/{id}/anular", ventaId))
                .andExpect(status().isCreated());

        assertThat(ventaRepository.findAll())
                .allSatisfy(venta -> assertThat(venta.getUsuario().getId()).isEqualTo(USUARIO_A_ID));
    }

    private MockHttpSession iniciarSesion(String dni, String contrasena) throws Exception {
        MvcResult resultado = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"dni":"%s","contrasena":"%s"}
                                """.formatted(dni, contrasena)))
                .andExpect(status().isOk())
                .andReturn();
        return (MockHttpSession) resultado.getRequest().getSession(false);
    }

    private void registrarVenta(MockHttpSession sesion, String producto) throws Exception {
        mockMvc.perform(post("/ventas")
                        .session(sesion)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ventaJson(producto)))
                .andExpect(status().isCreated());
    }

    private String ventaJson(String producto) {
        return """
                {"producto":"%s","monto":2500,"cantidad":1,"idTipoCobro":"%s"}
                """.formatted(producto, TIPO_COBRO_ID);
    }

    private UsuarioEntity usuario(UUID id, String dni, String nombre, String apellido, String contrasena) {
        return new UsuarioEntity(
                id,
                apellido,
                nombre,
                dni,
                dni + "@example.test",
                "11" + dni,
                new BCryptPasswordEncoder().encode(contrasena),
                LocalDateTime.of(2026, 9, 19, 10, 0),
                "SUPER_ADMIN"
        );
    }
}
