package ar.edu.unq.unqash.usuario;

import ar.edu.unq.unqash.persistencia.UsuarioEntity;
import ar.edu.unq.unqash.persistencia.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UsuarioService {

    private static final String USUARIO_ALTA = "SUPER_ADMIN";

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public UsuarioResponse crear(UsuarioRequest request) {
        validarUnicidad(request);

        UsuarioEntity usuario = new UsuarioEntity(
                UUID.randomUUID(),
                request.apellido().trim(),
                request.nombre().trim(),
                request.dni(),
                request.email().trim(),
                request.telefono(),
                passwordEncoder.encode(request.contrasena()),
                LocalDateTime.now(),
                USUARIO_ALTA,
                request.esGestor()
        );

        return UsuarioResponse.desdeModelo(usuarioRepository.save(usuario));
    }

    private void validarUnicidad(UsuarioRequest request) {
        if (usuarioRepository.existsByDni(request.dni())) {
            throw new IllegalArgumentException("DNI ya registrado");
        }
        if (usuarioRepository.existsByEmail(request.email().trim())) {
            throw new IllegalArgumentException("email ya registrado");
        }
        if (usuarioRepository.existsByTelefono(request.telefono())) {
            throw new IllegalArgumentException("teléfono ya registrado");
        }
    }
}
