package ar.edu.unq.unqash.venta;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ar.edu.unq.unqash.persistencia.VentaRepository;

@SpringBootTest
class VentaServiceTest {

    private static final UUID EFECTIVO_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Autowired
    private VentaService service;

    @Autowired
    private VentaRepository ventaRepository;

    @Test
    void registraUnaVentaConTipoDeCobro() {
        Venta venta = service.registrar(new VentaRequest("Café", new BigDecimal("2500"), 2, EFECTIVO_ID));

        assertThat(venta.id()).isNotBlank();
        assertThat(venta.producto()).isEqualTo("Café");
        assertThat(venta.monto()).isEqualByComparingTo("2500");
        assertThat(venta.cantidad()).isEqualTo(2);
        assertThat(venta.total()).isEqualByComparingTo("2500");
        assertThat(venta.idTipoCobro()).isEqualTo(EFECTIVO_ID);
        assertThat(ventaRepository.findById(UUID.fromString(venta.id()))).isPresent();
    }

    @Test
    void rechazaUnaVentaCuandoFaltaUnCampoObligatorio() {
        assertThatThrownBy(() -> service.registrar(new VentaRequest(null, new BigDecimal("2500"), 2, UUID.randomUUID())))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("producto es obligatorio");
    }
}
