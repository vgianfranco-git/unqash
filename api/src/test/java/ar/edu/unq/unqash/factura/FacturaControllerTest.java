package ar.edu.unq.unqash.factura;

import ar.edu.unq.unqash.persistencia.FacturaFotoRepository;
import ar.edu.unq.unqash.persistencia.FacturaRepository;
import ar.edu.unq.unqash.persistencia.UsuarioEntity;
import ar.edu.unq.unqash.persistencia.UsuarioRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:factura_controlador;MODE=PostgreSQL;DB_CLOSE_DELAY=-1"
})
class FacturaControllerTest {

    private static final String DNI = "40123456";
    private static final String CONTRASENA = "ClavePrueba1!";

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private FacturaFotoRepository facturaFotoRepository;

    private MockMvc mockMvc;
    private MockHttpSession sesion;

    @BeforeEach
    void setUp() throws Exception {
        facturaFotoRepository.deleteAll();
        facturaRepository.deleteAll();
        usuarioRepository.deleteAll();
        usuarioRepository.save(new UsuarioEntity(
                UUID.randomUUID(),
                "Gestora",
                "Ana",
                DNI,
                "ana.factura@unqash.com.ar",
                "1140123456",
                new BCryptPasswordEncoder().encode(CONTRASENA),
                LocalDateTime.of(2026, 9, 19, 10, 0),
                "SUPER_ADMIN",
                true
        ));
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        sesion = iniciarSesion();
    }

    @Test
    void creaUnaFacturaSinFoto() throws Exception {
        mockMvc.perform(multipart("/facturas")
                        .param("proveedor", "Proveedor SA")
                        .param("monto", "1500.00")
                        .param("fecha", "01/09/2026")
                        .param("detalles", "Compra de insumos")
                        .session(sesion))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.proveedor").value("Proveedor SA"));
    }

    @Test
    void creaUnaFacturaConFotoJpgValidaYQuedaAsociada() throws Exception {
        MvcResult resultado = mockMvc.perform(multipart("/facturas")
                        .file(new MockMultipartFile("foto", "comprobante.jpg", "image/jpeg", new byte[]{1, 2, 3}))
                        .param("proveedor", "Proveedor SA")
                        .param("monto", "1500.00")
                        .param("fecha", "01/09/2026")
                        .param("detalles", "Compra de insumos")
                        .session(sesion))
                .andExpect(status().isCreated())
                .andReturn();

        String facturaId = JsonPath.read(resultado.getResponse().getContentAsString(), "$.id");

        assertThat(facturaFotoRepository.findByFactura_Id(UUID.fromString(facturaId)))
                .hasValueSatisfying(foto -> assertThat(foto.getTipoContenido()).isEqualTo("image/jpeg"));
    }

    @Test
    void rechazaTodoSiElFormatoDeLaFotoEsInvalido() throws Exception {
        mockMvc.perform(multipart("/facturas")
                        .file(new MockMultipartFile("foto", "comprobante.txt", "text/plain", new byte[]{1, 2, 3}))
                        .param("proveedor", "Proveedor SA")
                        .param("monto", "1500.00")
                        .param("fecha", "01/09/2026")
                        .param("detalles", "Compra de insumos")
                        .session(sesion))
                .andExpect(status().isBadRequest());

        assertThat(facturaRepository.findAll()).isEmpty();
        assertThat(facturaFotoRepository.findAll()).isEmpty();
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
}
