package ar.edu.unq.unqash.persistencia;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
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
class PersistenciaFacturaFotoTest {

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private FacturaFotoRepository facturaFotoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void guardaUnaFotoAsociadaAUnaFacturaExistente() {
        UsuarioEntity usuario = usuarioRepository.save(new UsuarioEntity(
                UUID.randomUUID(),
                "Gestor",
                "Agustín",
                "41123456",
                "agustin.Gestor@unqash.com.ar",
                "1140123457",
                "hash",
                LocalDateTime.of(2026, 9, 19, 10, 0),
                "SUPER_ADMIN",
                true
        ));
        FacturaEntity factura = facturaRepository.save(new FacturaEntity(
                UUID.randomUUID(),
                "Proveedor SA",
                new BigDecimal("1500.00"),
                LocalDate.of(2026, 9, 1),
                "Compra de insumos",
                usuario
        ));

        byte[] contenido = new byte[]{1, 2, 3, 4};
        FacturaFotoEntity foto = facturaFotoRepository.save(new FacturaFotoEntity(
                UUID.randomUUID(),
                factura,
                "comprobante.jpg",
                "image/jpeg",
                contenido
        ));

        assertThat(facturaFotoRepository.findById(foto.getId()))
                .hasValueSatisfying(fotoGuardada -> {
                    assertThat(fotoGuardada.getFactura().getId()).isEqualTo(factura.getId());
                    assertThat(fotoGuardada.getNombreArchivo()).isEqualTo("comprobante.jpg");
                    assertThat(fotoGuardada.getTipoContenido()).isEqualTo("image/jpeg");
                    assertThat(fotoGuardada.getTamanioBytes()).isEqualTo(4L);
                    assertThat(fotoGuardada.getContenido()).isEqualTo(contenido);
                });

        assertThat(facturaFotoRepository.findByFactura_Id(factura.getId()))
                .hasValueSatisfying(fotoGuardada -> assertThat(fotoGuardada.getId()).isEqualTo(foto.getId()));
        assertThat(facturaFotoRepository.existsByFactura_Id(factura.getId())).isTrue();
    }
}
