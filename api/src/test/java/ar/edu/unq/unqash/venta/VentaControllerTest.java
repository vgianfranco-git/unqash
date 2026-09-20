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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:venta_controlador;MODE=PostgreSQL;DB_CLOSE_DELAY=-1"
})
class VentaControllerTest {

    private static final UUID EFECTIVO_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final String DNI = "40123456";
    private static final String CONTRASENA = "ClavePrueba1!";

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TipoCobroRepository tipoCobroRepository;

    private MockMvc mockMvc;
    private MockHttpSession sesion;

    @BeforeEach
    void setUp() throws Exception {
        ventaRepository.deleteAll();
        usuarioRepository.deleteAll();
        tipoCobroRepository.deleteAll();
        tipoCobroRepository.save(new TipoCobro(EFECTIVO_ID, "Efectivo"));
        usuarioRepository.save(new UsuarioEntity(
                UUID.fromString("00000000-0000-0000-0000-000000000010"),
                "Vendedora",
                "Ana",
                DNI,
                "ana.controlador@example.test",
                "1140123456",
                new BCryptPasswordEncoder().encode(CONTRASENA),
                LocalDateTime.of(2026, 9, 19, 10, 0),
                "SUPER_ADMIN"
        ));
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        sesion = iniciarSesion();
    }

    @Test
    void creaUnaVenta() throws Exception {
        mockMvc.perform(post("/ventas")
                        .session(sesion)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ventaJson("Café", EFECTIVO_ID)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.producto").value("Café"))
                .andExpect(jsonPath("$.monto").value(2500))
                .andExpect(jsonPath("$.cantidad").value(2))
                .andExpect(jsonPath("$.total").value(2500))
                .andExpect(jsonPath("$.idTipoCobro").value(EFECTIVO_ID.toString()));
    }

    @Test
    void rechazaUnaVentaConUnCampoFaltante() throws Exception {
        mockMvc.perform(post("/ventas")
                        .session(sesion)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"monto":2500,"cantidad":2}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rechazaUnaVentaConUnTipoDeCobroInexistente() throws Exception {
        mockMvc.perform(post("/ventas")
                        .session(sesion)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ventaJson("Café", UUID.fromString("11111111-1111-1111-1111-111111111111"))))
                .andExpect(status().isBadRequest());
    }

    private MockHttpSession iniciarSesion() throws Exception {
        MvcResult resultado = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"dni":"%s","contrasena":"%s"}
                                """.formatted(DNI, CONTRASENA)))
                .andExpect(status().isOk())
                .andReturn();
        return (MockHttpSession) resultado.getRequest().getSession(false);
    }

    private String ventaJson(String producto, UUID idTipoCobro) {
        return """
                {"producto":"%s","monto":2500,"cantidad":2,"idTipoCobro":"%s"}
                """.formatted(producto, idTipoCobro);
    }
}
