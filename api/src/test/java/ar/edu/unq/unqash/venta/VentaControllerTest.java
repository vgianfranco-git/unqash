package ar.edu.unq.unqash.venta;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class VentaControllerTest {

    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new VentaController(new VentaService()))
            .build();

    @Test
    void creaUnaVenta() throws Exception {
        mockMvc.perform(post("/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"producto":"Café","monto":2500,"cantidad":2}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.C_ID").isNotEmpty())
                .andExpect(jsonPath("$.producto").value("Café"))
                .andExpect(jsonPath("$.monto").value(2500))
                .andExpect(jsonPath("$.cantidad").value(2))
                .andExpect(jsonPath("$.total").value(2500));
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
}
