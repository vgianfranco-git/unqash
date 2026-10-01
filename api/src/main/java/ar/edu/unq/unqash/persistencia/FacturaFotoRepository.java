package ar.edu.unq.unqash.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FacturaFotoRepository extends JpaRepository<FacturaFotoEntity, UUID> {

    Optional<FacturaFotoEntity> findByFactura_Id(UUID facturaId);

    boolean existsByFactura_Id(UUID facturaId);
}
