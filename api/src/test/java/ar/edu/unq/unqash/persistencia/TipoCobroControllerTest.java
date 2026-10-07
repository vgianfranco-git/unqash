package ar.edu.unq.unqash.persistencia;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:tipo_cobro_controlador;MODE=PostgreSQL;DB_CLOSE_DELAY=-1"
})
class TipoCobroControllerTest {

    @Autowired
    private TipoCobroRepository tipoCobroRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        tipoCobroRepository.deleteAll();
        tipoCobroRepository.saveAll(List.of(
                new TipoCobro(UUID.fromString("00000000-0000-0000-0000-000000000001"), "Efectivo"),
                new TipoCobro(UUID.fromString("00000000-0000-0000-0000-000000000002"), "Transferencia"),
                new TipoCobro(UUID.fromString("00000000-0000-0000-0000-000000000003"), "Otro")
        ));
        mockMvc = MockMvcBuilders.standaloneSetup(new TipoCobroController(tipoCobroRepository)).build();
    }

    @Test
    void listaLosTiposDeCobroDisponibles() throws Exception {
        mockMvc.perform(get("/tipos-cobro"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].descripcion").value("Efectivo"));
    }
}
