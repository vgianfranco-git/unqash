package ar.edu.unq.unqash.usuario;

import java.util.List;

public record UsuarioHistorialPageResponse(
        List<UsuarioResponse> usuarios,
        int numPag,
        int totalPag,
        long totalRegistros
) {
}
