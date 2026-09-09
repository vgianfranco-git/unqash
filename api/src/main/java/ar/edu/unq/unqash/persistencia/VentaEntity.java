package ar.edu.unq.unqash.persistencia;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "UNQASH_VENTA")
public class VentaEntity {

    @Id
    @Column(name = "C_ID", nullable = false)
    private UUID id;

    @Column(name = "D_PRODUCTO", nullable = false)
    private String producto;

    @Column(name = "N_MONTO", nullable = false, precision = 15, scale = 2)
    private BigDecimal monto;

    @Column(name = "N_CANTIDAD", nullable = false)
    private Integer cantidad;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "C_ID_TIPO_COBRO", nullable = false)
    private TipoCobro tipoDeCobro;


    @Column(name = "FECHA", nullable = false)
    private LocalDate fecha;
    @Column(name = "HORA", nullable = false)
    private LocalTime hora;

    protected VentaEntity() {
    }

    public VentaEntity(UUID id, String producto, BigDecimal monto, Integer cantidad, TipoCobro tipoDeCobro) {
        this.id = id;
        this.producto = producto;
        this.monto = monto;
        this.cantidad = cantidad;
        this.tipoDeCobro = tipoDeCobro;
        fecha = LocalDate.now();
        hora = LocalTime.now();
    }

    public UUID id() {
        return id;
    }

    public TipoCobro tipoDeCobro() {
        return tipoDeCobro;
    }

    public UUID getId() {
        return id;
    }

    public String getProducto() {
        return producto;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public TipoCobro getTipoDeCobro() {
        return tipoDeCobro;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public LocalTime getHora() {
        return hora;
    }
}
