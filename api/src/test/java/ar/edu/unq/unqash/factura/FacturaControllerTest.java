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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private FacturaService facturaService;

    @Autowired
    private PlatformTransactionManager transactionManager;

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

    @Test
    void unGestorAnulaUnaFacturaYPersisteElEstado() throws Exception {
        String facturaId = crearFactura();

        mockMvc.perform(put("/facturas/{idFactura}/anular", facturaId).session(sesion))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(facturaId))
                .andExpect(jsonPath("$.estado").value("ANULADA"));

        assertThat(jdbcTemplate.queryForObject(
                "SELECT C_ESTADO FROM UNQASH_FACTURA WHERE C_ID = ?", String.class,
                UUID.fromString(facturaId))).isEqualTo("ANULADA");
    }

    @Test
    void creaFacturasActivas() throws Exception {
        String facturaId = crearFactura();
        assertThat(facturaRepository.findById(UUID.fromString(facturaId)))
                .hasValueSatisfying(factura -> assertThat(factura.getEstado()).isEqualTo("ACTIVA"));
    }

    @Test
    void elDetalleReflejaLaFacturaAnulada() throws Exception {
        String facturaId = crearFactura();
        mockMvc.perform(put("/facturas/{idFactura}/anular", facturaId).session(sesion))
                .andExpect(status().isOk());

        mockMvc.perform(get("/facturas/{idFactura}", facturaId).session(sesion))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ANULADA"));
    }

    @Test
    void elHistorialReflejaLaFacturaAnulada() throws Exception {
        String facturaId = crearFactura();
        mockMvc.perform(put("/facturas/{idFactura}/anular", facturaId).session(sesion))
                .andExpect(status().isOk());

        mockMvc.perform(get("/facturas").session(sesion))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.facturas[0].estado").value("ANULADA"));
    }

    @Test
    void rechazaAnularUnaFacturaInexistente() throws Exception {
        mockMvc.perform(put("/facturas/{idFactura}/anular", UUID.randomUUID()).session(sesion))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Factura inexistente."));
    }

    @Test
    void rechazaLaSegundaAnulacion() throws Exception {
        String facturaId = crearFactura();
        mockMvc.perform(put("/facturas/{idFactura}/anular", facturaId).session(sesion))
                .andExpect(status().isOk());

        mockMvc.perform(put("/facturas/{idFactura}/anular", facturaId).session(sesion))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("La factura ya está anulada."));

        assertThat(facturaRepository.findById(UUID.fromString(facturaId)))
                .hasValueSatisfying(factura -> assertThat(factura.getEstado()).isEqualTo("ANULADA"));
    }

    @Test
    void soloUnaDeDosAnulacionesConcurrentesPuedeConfirmarse() throws Exception {
        UUID facturaId = UUID.fromString(crearFactura());
        CyclicBarrier lecturas = new CyclicBarrier(2);
        Callable<Boolean> anular = () -> {
            try {
                return new TransactionTemplate(transactionManager).execute(transaction -> {
                    assertThat(facturaRepository.findById(facturaId).orElseThrow().getEstado())
                            .isEqualTo("ACTIVA");
                    try {
                        lecturas.await(5, TimeUnit.SECONDS);
                    } catch (Exception exception) {
                        throw new IllegalStateException(exception);
                    }
                    facturaService.anularFactura(facturaId);
                    return true;
                });
            } catch (IllegalArgumentException exception) {
                return false;
            }
        };

        try (var executor = Executors.newFixedThreadPool(2)) {
            var primera = executor.submit(anular);
            var segunda = executor.submit(anular);
            assertThat(new Boolean[]{primera.get(10, TimeUnit.SECONDS), segunda.get(10, TimeUnit.SECONDS)})
                    .containsExactlyInAnyOrder(true, false);
        }
        assertThat(facturaRepository.findById(facturaId))
                .hasValueSatisfying(factura -> assertThat(factura.getEstado()).isEqualTo("ANULADA"));
    }

    @Test
    void unaEdicionConEstadoAnteriorNoReactivaUnaFacturaAnulada() throws Exception {
        UUID facturaId = UUID.fromString(crearFactura());
        TransactionTemplate anulacionIndependiente = new TransactionTemplate(transactionManager);
        anulacionIndependiente.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        MvcResult resultado = new TransactionTemplate(transactionManager).execute(edicion -> {
            assertThat(facturaRepository.findById(facturaId).orElseThrow().getEstado()).isEqualTo("ACTIVA");
            anulacionIndependiente.executeWithoutResult(anulacion -> facturaService.anularFactura(facturaId));
            try {
                return mockMvc.perform(multipart("/facturas/{facturaId}", facturaId)
                                .with(request -> {
                                    request.setMethod("PUT");
                                    return request;
                                })
                                .param("proveedor", "Proveedor editado")
                                .param("monto", "2000.00")
                                .param("fecha", "02/09/2026")
                                .param("detalles", "Detalle editado")
                                .session(sesion))
                        .andExpect(status().isOk())
                        .andReturn();
            } catch (Exception exception) {
                throw new IllegalStateException(exception);
            }
        });

        assertThat(facturaRepository.findById(facturaId)).hasValueSatisfying(factura -> {
            assertThat(factura.getProveedor()).isEqualTo("Proveedor editado");
            assertThat(factura.getMonto()).isEqualByComparingTo("2000.00");
            assertThat(factura.getEstado()).isEqualTo("ANULADA");
        });
        mockMvc.perform(put("/facturas/{idFactura}/anular", facturaId).session(sesion))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("La factura ya está anulada."));
        assertThat((String) JsonPath.read(resultado.getResponse().getContentAsString(), "$.estado"))
                .isEqualTo("ANULADA");
    }

    @Test
    void rechazaAnularSinSesionYSinModificarLaFactura() throws Exception {
        String facturaId = crearFactura();
        mockMvc.perform(put("/facturas/{idFactura}/anular", facturaId))
                .andExpect(status().isUnauthorized());

        assertThat(facturaRepository.findById(UUID.fromString(facturaId)))
                .hasValueSatisfying(factura -> assertThat(factura.getEstado()).isEqualTo("ACTIVA"));
    }

    @Test
    void rechazaAnularSinRolGestorYSinModificarLaFactura() throws Exception {
        String facturaId = crearFactura();
        UsuarioEntity usuario = usuarioRepository.save(new UsuarioEntity(
                UUID.randomUUID(), "Operadora", "Eva", "40987654", "eva.factura@unqash.com.ar",
                "1198765432", new BCryptPasswordEncoder().encode(CONTRASENA),
                LocalDateTime.of(2026, 9, 19, 10, 0), "SUPER_ADMIN", false));
        MockHttpSession sesionOperadora = new MockHttpSession();
        sesionOperadora.setAttribute("usuarioId", usuario.getId().toString());

        mockMvc.perform(put("/facturas/{idFactura}/anular", facturaId).session(sesionOperadora))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Usuario sin permisos"));

        assertThat(facturaRepository.findById(UUID.fromString(facturaId)))
                .hasValueSatisfying(factura -> assertThat(factura.getEstado()).isEqualTo("ACTIVA"));
    }

    private String crearFactura() throws Exception {
        MvcResult resultado = mockMvc.perform(multipart("/facturas")
                        .param("proveedor", "Proveedor SA")
                        .param("monto", "1500.00")
                        .param("fecha", "01/09/2026")
                        .param("detalles", "Compra de insumos")
                        .session(sesion))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("ACTIVA"))
                .andReturn();
        return JsonPath.read(resultado.getResponse().getContentAsString(), "$.id");
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
