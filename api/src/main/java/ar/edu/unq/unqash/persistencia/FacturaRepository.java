package ar.edu.unq.unqash.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FacturaRepository extends JpaRepository<FacturaEntity, UUID> {

}
