package ar.edu.unq.unqash.usuario;

import ar.edu.unq.unqash.auth.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final AuthService authService;

    public UsuarioController(UsuarioService usuarioService, AuthService authService) {
        this.usuarioService = usuarioService;
        this.authService = authService;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> crear(@Valid @RequestBody UsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.crear(request));
    }

    @GetMapping("/historial")
    public UsuarioHistorialPageResponse historial(@RequestParam(defaultValue = "1") int page,
                                                  @RequestParam(defaultValue = "3") int size,
                                                  HttpServletRequest httpRequest) {
        usuarioService.validarGestor(authService.recuperarUsuarioAutenticado(httpRequest));
        Page<UsuarioResponse> historial = usuarioService.recuperarHistorial(page, size);
        return new UsuarioHistorialPageResponse(historial.getContent(), historial.getNumber() + 1,
                historial.getTotalPages(), historial.getTotalElements());
    }

    @GetMapping("/{idUsuario}")
    public UsuarioResponse obtener(@PathVariable UUID idUsuario, HttpServletRequest httpRequest) {
        usuarioService.validarGestor(authService.recuperarUsuarioAutenticado(httpRequest));
        return usuarioService.obtenerUsuario(idUsuario);
    }

    @PutMapping("/{idUsuario}")
    public UsuarioResponse editar(@Valid @RequestBody UsuarioEdicionRequest request,
                                  @PathVariable UUID idUsuario, HttpServletRequest httpRequest) {
        usuarioService.validarGestor(authService.recuperarUsuarioAutenticado(httpRequest));
        return usuarioService.editar(idUsuario, request);
    }
}
