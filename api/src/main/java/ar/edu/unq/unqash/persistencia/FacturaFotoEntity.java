package ar.edu.unq.unqash.persistencia;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "UNQASH_FACTURA_FOTO")
public class FacturaFotoEntity {

    @Id
    @Column(name = "C_ID", nullable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "C_ID_FACTURA", nullable = false, unique = true)
    private FacturaEntity factura;

    @Column(name = "D_NOMBRE_ARCHIVO", nullable = false)
    private String nombreArchivo;

    @Column(name = "D_TIPO_CONTENIDO", nullable = false)
    private String tipoContenido;

    @Column(name = "N_TAMANIO_BYTES", nullable = false)
    private Long tamanioBytes;

    @Column(name = "CONTENIDO", nullable = false)
    private byte[] contenido;

    @Column(name = "FE_FECHA_HORA_ALTA", nullable = false)
    private LocalDateTime fechaHoraAlta;

    protected FacturaFotoEntity() {
    }

    public FacturaFotoEntity(UUID id, FacturaEntity factura, String nombreArchivo, String tipoContenido,
                              byte[] contenido) {
        this.id = id;
        this.factura = factura;
        this.nombreArchivo = nombreArchivo;
        this.tipoContenido = tipoContenido;
        this.contenido = contenido;
        this.tamanioBytes = (long) contenido.length;
        this.fechaHoraAlta = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public FacturaEntity getFactura() {
        return factura;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public String getTipoContenido() {
        return tipoContenido;
    }

    public Long getTamanioBytes() {
        return tamanioBytes;
    }

    public byte[] getContenido() {
        return contenido;
    }

    public LocalDateTime getFechaHoraAlta() {
        return fechaHoraAlta;
    }

    public void actualizarContenido(String nombreArchivo, String tipoContenido, byte[] contenido) {
        this.nombreArchivo = nombreArchivo;
        this.tipoContenido = tipoContenido;
        this.contenido = contenido;
        this.tamanioBytes = (long) contenido.length;
        this.fechaHoraAlta = LocalDateTime.now();
    }
}
