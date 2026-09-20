package ar.edu.unq.unqash.auth;

import ar.edu.unq.unqash.usuario.UsuarioResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioResponse> iniciarSesion(@Valid @RequestBody AuthRequest request,
                                                          HttpServletRequest httpRequest) {
        return ResponseEntity.ok(authService.iniciarSesion(request, httpRequest));
    }

    @GetMapping("/sesion")
    public ResponseEntity<UsuarioResponse> recuperarSesion(HttpServletRequest httpRequest) {
        return ResponseEntity.ok(authService.recuperarSesion(httpRequest));
    }

    @DeleteMapping("/sesion")
    public ResponseEntity<Void> cerrarSesion(HttpServletRequest httpRequest) {
        authService.cerrarSesion(httpRequest);
        return ResponseEntity.noContent().build();
    }
}
