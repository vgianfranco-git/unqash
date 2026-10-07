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

    @Column(name = "C_ESTADO", nullable = false, length = 10, updatable = false)
    private String estado = "ACTIVA";

    @Column(name = "FE_FECHA_HORA_ALTA", nullable = false)
    private LocalDateTime fechaHotaAlta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "C_ID_USUARIO_ALTA", nullable = false)
    private UsuarioEntity usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "C_ID_USUARIO_MODIFICACION", nullable = true)
    private UsuarioEntity usuarioModificacion;

    @Column(name = "FE_FECHA_HORA_MODIFICACION", nullable = true)
    private LocalDateTime fechaHoraModificacion;

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

    public String getEstado() {
        return estado;
    }

    public LocalDateTime getFechaHotaAlta() {
        return fechaHotaAlta;
    }

    public UsuarioEntity getUsuario() {
        return usuario;
    }

    public UsuarioEntity getUsuarioModificacion() {
        return usuarioModificacion;
    }

    public LocalDateTime getFechaHoraModificacion() {
        return fechaHoraModificacion;
    }

    public void actualizarDatos(String proveedor, BigDecimal monto, LocalDate fechaFactura, String detalle,
                                 UsuarioEntity usuarioModificacion) {
        this.proveedor = proveedor;
        this.monto = monto;
        this.fechaFactura = fechaFactura;
        this.detalle = detalle;
        this.usuarioModificacion = usuarioModificacion;
        this.fechaHoraModificacion = LocalDateTime.now();
    }
}
