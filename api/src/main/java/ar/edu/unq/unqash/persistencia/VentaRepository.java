package ar.edu.unq.unqash.persistencia;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VentaRepository extends JpaRepository<VentaEntity, UUID> {

    Page<VentaEntity> findAllByUsuarioId(UUID usuarioId, Pageable pageable);
}
