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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:venta_servicio;MODE=PostgreSQL;DB_CLOSE_DELAY=-1"
})
class VentaServiceTest {

    private static final UUID EFECTIVO_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID USUARIO_ID = UUID.fromString("00000000-0000-0000-0000-000000000010");

    @Autowired
    private VentaService service;

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private TipoCobroRepository tipoCobroRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private UsuarioEntity usuario;

    @BeforeEach
    void setUp() {
        ventaRepository.deleteAll();
        usuarioRepository.deleteAll();
        tipoCobroRepository.deleteAll();

        tipoCobroRepository.save(new TipoCobro(EFECTIVO_ID, "Efectivo"));
        usuario = usuarioRepository.save(new UsuarioEntity(
                USUARIO_ID,
                "Vendedora",
                "Ana",
                "40123456",
                "ana.servicio@example.test",
                "1140123456",
                "hash",
                LocalDateTime.of(2026, 9, 19, 10, 0),
                "SUPER_ADMIN"
        ));
    }

    @Test
    void registraUnaVentaConTipoDeCobroYUsuario() {
        Venta venta = service.registrar(new VentaRequest("Café", new BigDecimal("2500"), 2, EFECTIVO_ID), usuario);

        assertThat(venta.id()).isNotBlank();
        assertThat(venta.producto()).isEqualTo("Café");
        assertThat(venta.monto()).isEqualByComparingTo("2500");
        assertThat(venta.cantidad()).isEqualTo(2);
        assertThat(venta.total()).isEqualByComparingTo("2500");
        assertThat(venta.idTipoCobro()).isEqualTo(EFECTIVO_ID);
        assertThat(venta.usuario().id()).isEqualTo(USUARIO_ID);
        assertThat(ventaRepository.findById(UUID.fromString(venta.id())))
                .hasValueSatisfying(ventaGuardada ->
                        assertThat(ventaGuardada.getUsuario().getId()).isEqualTo(USUARIO_ID)
                );
    }

    @Test
    void rechazaUnaVentaCuandoFaltaUnCampoObligatorio() {
        assertThatThrownBy(() -> service.registrar(
                new VentaRequest(null, new BigDecimal("2500"), 2, UUID.randomUUID()), usuario
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("producto es obligatorio");
    }
}
