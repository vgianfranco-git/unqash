package ar.edu.unq.unqash.persistencia;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
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

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void guardaUnaVentaConUnTipoDeCobroExistente() {
        TipoCobro tipoDeCobro = tipoCobroRepository.save(new TipoCobro(UUID.randomUUID(), "Tipo de prueba"));
        UsuarioEntity usuario = usuarioRepository.save(new UsuarioEntity(
                UUID.randomUUID(),
                "Vendedora",
                "Ana",
                "40123456",
                "ana.persistencia@example.test",
                "1140123456",
                "hash",
                LocalDateTime.of(2026, 9, 19, 10, 0),
                "SUPER_ADMIN"
        ));
        VentaEntity venta = ventaRepository.save(new VentaEntity(
                UUID.randomUUID(),
                "Café",
                new BigDecimal("2500"),
                2,
                tipoDeCobro,
                usuario
        ));

        assertThat(ventaRepository.findById(venta.id()))
                .hasValueSatisfying(ventaGuardada -> {
                    assertThat(ventaGuardada.tipoDeCobro().id()).isEqualTo(tipoDeCobro.id());
                    assertThat(ventaGuardada.getUsuario().getId()).isEqualTo(usuario.getId());
                });
    }
}
