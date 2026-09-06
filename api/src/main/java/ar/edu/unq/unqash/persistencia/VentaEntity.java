package ar.edu.unq.unqash.persistencia;

import java.math.BigDecimal;
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
    private UUID cId;

    @Column(name = "D_PRODUCTO", nullable = false)
    private String producto;

    @Column(name = "N_MONTO", nullable = false, precision = 15, scale = 2)
    private BigDecimal monto;

    @Column(name = "N_CANTIDAD", nullable = false)
    private Integer cantidad;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "C_ID_TIPO_COBRO", nullable = false)
    private TipoCobro tipoDeCobro;

    protected VentaEntity() {
    }

    public VentaEntity(UUID cId, String producto, BigDecimal monto, Integer cantidad, TipoCobro tipoDeCobro) {
        this.cId = cId;
        this.producto = producto;
        this.monto = monto;
        this.cantidad = cantidad;
        this.tipoDeCobro = tipoDeCobro;
    }

    public UUID cId() {
        return cId;
    }

    public TipoCobro tipoDeCobro() {
        return tipoDeCobro;
    }
}
