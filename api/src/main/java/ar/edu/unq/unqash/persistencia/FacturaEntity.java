package ar.edu.unq.unqash.persistencia;


import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "UNQASH_FACTURA")
public class FacturaEntity {

    @Id
    @Column(name = "C_ID", nullable = false)
    private UUID id;

    @Column(name = "D_PROVEEDOR", nullable = false)
    private String proveedor;

    @Column(name = "N_MONTO", nullable = false)
    private BigDecimal monto;

    @Column(name = "FH_FECHA_FACTURA", nullable = false)
    private LocalDate fechaFactura;

    @Column(name = "D_DETALLE")
    private String detalle;

    @Column(name = "FE_FECHA_HORA_ALTA", nullable = false)
    private LocalDateTime fechaHotaAlta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "C_ID_USUARIO_ALTA", nullable = false)
    private UsuarioEntity usuario;

    protected FacturaEntity(){}

    public FacturaEntity(UUID id, String proveedor, BigDecimal monto, LocalDate fechaFactura, String detalle,
                         UsuarioEntity usuario) {
        this.id = id;
        this.proveedor = proveedor;
        this.monto = monto;
        this.fechaFactura = fechaFactura;
        this.detalle = detalle;
        this.fechaHotaAlta = LocalDateTime.now();
        this.usuario = usuario;
    }

    public UUID getId() {
        return id;
    }

    public String getProveedor() {
        return proveedor;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public LocalDate getFechaFactura() {
        return fechaFactura;
    }

    public String getDetalle() {
        return detalle;
    }

    public LocalDateTime getFechaHotaAlta() {
        return fechaHotaAlta;
    }

    public UsuarioEntity getUsuario() {
        return usuario;
    }
}
