package ar.edu.unq.unqash.venta;

import ar.edu.unq.unqash.persistencia.UsuarioEntity;

import java.util.UUID;

public record UsuarioVentaResponse(
        UUID id,
        String nombre,
        String apellido
) {

    public static UsuarioVentaResponse desdeModelo(UsuarioEntity usuario) {
        return new UsuarioVentaResponse(usuario.getId(), usuario.getNombre(), usuario.getApellido());
    }
}
