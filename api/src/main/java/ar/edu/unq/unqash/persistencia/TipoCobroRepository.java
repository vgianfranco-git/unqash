package ar.edu.unq.unqash.persistencia;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoCobroRepository extends JpaRepository<TipoCobro, UUID> {

    List<TipoCobro> findAllByOrderByDescripcionAsc();
}
