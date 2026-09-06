package ar.edu.unq.unqash.persistencia;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "UNQASH_TIPO_COBRO")
public class TipoCobro {

    @Id
    @Column(name = "C_ID", nullable = false)
    private UUID cId;

    @Column(name = "D_DESCRIPCION", nullable = false, unique = true)
    private String descripcion;

    protected TipoCobro() {
    }

    public TipoCobro(UUID cId, String descripcion) {
        this.cId = cId;
        this.descripcion = descripcion;
    }

    public UUID cId() {
        return cId;
    }

    public String descripcion() {
        return descripcion;
    }
}
