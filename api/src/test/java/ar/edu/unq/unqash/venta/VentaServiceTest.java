package ar.edu.unq.unqash.venta;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class VentaServiceTest {

    @Test
    void registraUnaVentaConIdGeneradoYTotalIgualAlMonto() {
        VentaService service = new VentaService();

        Venta venta = service.registrar(new VentaRequest("Café", new BigDecimal("2500"), 2));

        assertThat(venta.cId()).isNotBlank();
        assertThat(venta.producto()).isEqualTo("Café");
        assertThat(venta.monto()).isEqualByComparingTo("2500");
        assertThat(venta.cantidad()).isEqualTo(2);
        assertThat(venta.total()).isEqualByComparingTo("2500");
    }

    @Test
    void rechazaUnaVentaCuandoFaltaUnCampoObligatorio() {
        VentaService service = new VentaService();

        assertThatThrownBy(() -> service.registrar(new VentaRequest(null, new BigDecimal("2500"), 2)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("producto es obligatorio");
    }
}
