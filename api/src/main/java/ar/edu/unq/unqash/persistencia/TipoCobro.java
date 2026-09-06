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
    private UUID id;

    @Column(name = "D_DESCRIPCION", nullable = false, unique = true)
    private String descripcion;

    protected TipoCobro() {
    }

    public TipoCobro(UUID id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
    }

    public UUID id() {
        return id;
    }

    public String descripcion() {
        return descripcion;
    }
}
