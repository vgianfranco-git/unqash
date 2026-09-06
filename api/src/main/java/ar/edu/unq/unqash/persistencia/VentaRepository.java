package ar.edu.unq.unqash.persistencia;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VentaRepository extends JpaRepository<VentaEntity, UUID> {
}
