package ar.edu.unq.unqash.usuario;

import ar.edu.unq.unqash.persistencia.UsuarioEntity;

import java.time.LocalDateTime;
import java.util.UUID;

public record UsuarioResponse(
        UUID id,
        String apellido,
        String nombre,
        String dni,
        String email,
        String telefono,
        LocalDateTime fechaHoraAlta,
        String usuarioDeAlta
) {
    public static UsuarioResponse desdeModelo(UsuarioEntity usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getApellido(),
                usuario.getNombre(),
                usuario.getDni(),
                usuario.getEmail(),
                usuario.getTelefono(),
                usuario.getFechaHoraAlta(),
                usuario.getUsuarioAlta()
        );
    }
}
