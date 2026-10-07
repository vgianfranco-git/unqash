package ar.edu.unq.unqash.usuario;

import ar.edu.unq.unqash.persistencia.UsuarioEntity;
import ar.edu.unq.unqash.persistencia.UsuarioRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;
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

    public Page<UsuarioResponse> recuperarHistorial(int page, int size) {
        if (page < 1) {
            throw new IllegalArgumentException("El número de página debe ser mayor a 0.");
        }
        if (size < 1) {
            throw new IllegalArgumentException("El tamaño de página debe ser mayor a 0.");
        }
        PageRequest pagina = PageRequest.of(page - 1, size,
                Sort.by(Sort.Direction.DESC, "fechaHoraAlta", "id"));
        return usuarioRepository.findAll(pagina).map(UsuarioResponse::desdeModelo);
    }

    public UsuarioResponse obtenerUsuario(UUID idUsuario) {
        UsuarioEntity usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario inexistente."));
        return UsuarioResponse.desdeModelo(usuario);
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

    @Transactional
    public UsuarioResponse editar(UUID idUsuario, UsuarioEdicionRequest request) {
        UsuarioEntity original = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario inexistente."));
        validarUnicidad(idUsuario, request);
        String contrasenaHash = original.getContrasenaHash();
        if (request.contrasena() != null && !request.contrasena().isEmpty()) {
            contrasenaHash = passwordEncoder.encode(request.contrasena());
        }
        Boolean esGestor = request.esGestor() == null ? original.getEsGestor() : request.esGestor();
        original.actualizarDatos(request.apellido().trim(),
                request.nombre().trim(), request.dni(), request.email().trim(), request.telefono(),
                contrasenaHash, esGestor);
        try {
            return UsuarioResponse.desdeModelo(usuarioRepository.saveAndFlush(original));
        } catch (DataIntegrityViolationException exception) {
            throw traducirDuplicado(exception);
        }
    }

    private RuntimeException traducirDuplicado(DataIntegrityViolationException exception) {
        for (Throwable causa = exception; causa != null; causa = causa.getCause()) {
            if (causa instanceof ConstraintViolationException violacion && violacion.getConstraintName() != null) {
                String constraint = violacion.getConstraintName().toLowerCase(Locale.ROOT);
                if (constraint.contains("unqash_usuario_n_dni_key")) {
                    return new IllegalArgumentException("DNI ya registrado", exception);
                }
                if (constraint.contains("unqash_usuario_d_email_key")) {
                    return new IllegalArgumentException("email ya registrado", exception);
                }
                if (constraint.contains("unqash_usuario_n_telefono_key")) {
                    return new IllegalArgumentException("teléfono ya registrado", exception);
                }
            }
        }
        return exception;
    }

    private void validarUnicidad(UUID idUsuario, UsuarioEdicionRequest request) {
        if (usuarioRepository.existsByDniAndIdNot(request.dni(), idUsuario)) {
            throw new IllegalArgumentException("DNI ya registrado");
        }
        if (usuarioRepository.existsByEmailAndIdNot(request.email().trim(), idUsuario)) {
            throw new IllegalArgumentException("email ya registrado");
        }
        if (usuarioRepository.existsByTelefonoAndIdNot(request.telefono(), idUsuario)) {
            throw new IllegalArgumentException("teléfono ya registrado");
        }
    }

    public void validarGestor(UsuarioEntity usuario){
        if(!usuario.getEsGestor()){
            throw new IllegalArgumentException("Usuario sin permisos");
        }
    }
}
