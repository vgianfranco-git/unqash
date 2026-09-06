package ar.edu.unq.unqash.persistencia;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:unqash;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password="
})
@Transactional
class PersistenciaVentaTest {

    @Autowired
    private TipoCobroRepository tipoCobroRepository;

    @Autowired
    private VentaRepository ventaRepository;

    @Test
    void guardaUnaVentaConUnTipoDeCobroExistente() {
        TipoCobro tipoDeCobro = tipoCobroRepository.save(new TipoCobro(UUID.randomUUID(), "Tipo de prueba"));
        VentaEntity venta = ventaRepository.save(new VentaEntity(
                UUID.randomUUID(),
                "Café",
                new BigDecimal("2500"),
                2,
                tipoDeCobro
        ));

        assertThat(ventaRepository.findById(venta.cId()))
                .hasValueSatisfying(ventaGuardada ->
                        assertThat(ventaGuardada.tipoDeCobro().cId()).isEqualTo(tipoDeCobro.cId())
                );
    }
}
