package ar.edu.unq.unqash.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface FacturaRepository extends JpaRepository<FacturaEntity, UUID> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE FacturaEntity f SET f.estado = 'ANULADA' WHERE f.id = :id AND f.estado = 'ACTIVA'")
    int anularSiActiva(@Param("id") UUID id);
}
