package ar.edu.unq.unqash.persistencia;

import java.util.UUID;

public record TipoCobroResponse(
        UUID id,
        String descripcion
) {
}
