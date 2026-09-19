package ar.edu.unq.unqash.auth;

import ar.edu.unq.unqash.persistencia.UsuarioEntity;
import ar.edu.unq.unqash.persistencia.UsuarioRepository;
import ar.edu.unq.unqash.usuario.UsuarioResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {

    private static final String USUARIO_ID_SESSION_KEY = "usuarioId";

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public UsuarioResponse iniciarSesion(AuthRequest request, HttpServletRequest httpRequest) {
        UsuarioEntity usuario = usuarioRepository.findByDni(request.dni())
                .filter(usuarioEncontrado -> passwordEncoder.matches(request.contrasena(), usuarioEncontrado.getContrasenaHash()))
                .orElseThrow(CredencialesInvalidasException::new);

        HttpSession sesionAnterior = httpRequest.getSession(false);
        if (sesionAnterior != null) {
            sesionAnterior.invalidate();
        }

        HttpSession sesion = httpRequest.getSession(true);
        sesion.setAttribute(USUARIO_ID_SESSION_KEY, usuario.getId().toString());

        return UsuarioResponse.desdeModelo(usuario);
    }

    public UsuarioResponse recuperarSesion(HttpServletRequest httpRequest) {
        HttpSession sesion = httpRequest.getSession(false);
        if (sesion == null) {
            throw new SesionInactivaException();
        }

        Object usuarioId = sesion.getAttribute(USUARIO_ID_SESSION_KEY);
        if (!(usuarioId instanceof String id)) {
            throw new SesionInactivaException();
        }

        try {
            UsuarioEntity usuario = usuarioRepository.findById(UUID.fromString(id))
                    .orElseThrow(SesionInactivaException::new);
            return UsuarioResponse.desdeModelo(usuario);
        } catch (IllegalArgumentException exception) {
            throw new SesionInactivaException();
        }
    }
}
