package ar.edu.unq.unqash.venta;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class VentaControllerTest {

    private static final UUID EFECTIVO_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Autowired
    private VentaService ventaService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new VentaController(ventaService))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    void creaUnaVenta() throws Exception {
        mockMvc.perform(post("/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"producto":"Café","monto":2500,"cantidad":2,"idTipoCobro":"%s"}
                                """.formatted(EFECTIVO_ID)))
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
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"monto":2500,"cantidad":2}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rechazaUnaVentaConUnTipoDeCobroInexistente() throws Exception {
        mockMvc.perform(post("/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"producto":"Café","monto":2500,"cantidad":2,"idTipoCobro":"11111111-1111-1111-1111-111111111111"}
                                """))
                .andExpect(status().isBadRequest());
    }
}
