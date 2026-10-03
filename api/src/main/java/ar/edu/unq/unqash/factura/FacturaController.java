package ar.edu.unq.unqash.factura;

import ar.edu.unq.unqash.auth.AuthService;
import ar.edu.unq.unqash.persistencia.FacturaEntity;
import ar.edu.unq.unqash.persistencia.FacturaFotoEntity;
import ar.edu.unq.unqash.persistencia.UsuarioEntity;
import ar.edu.unq.unqash.usuario.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/facturas")
public class FacturaController {

    FacturaService facturaService;
    AuthService authService;
    UsuarioService usuarioService;

    public FacturaController(FacturaService facturaService, AuthService authService, UsuarioService usuarioService){
        this.facturaService = facturaService;
        this.authService = authService;
        this.usuarioService = usuarioService;
    }


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FacturaResponse> registrarFactura(@Valid @ModelAttribute FacturaRequest requestF,
                                                           @RequestParam(value = "foto", required = false) MultipartFile foto,
                                                           HttpServletRequest httpRequest){

        UsuarioEntity usuario = authService.recuperarUsuarioAutenticado(httpRequest);
        FacturaEntity factura = facturaService.addFactura(requestF, foto, usuario);

        return ResponseEntity.status(HttpStatus.CREATED).body(FacturaResponse.desdeModelo(factura));

    }

    @GetMapping
    public FacturaHistorialPageResponse listarFacturas(@RequestParam(defaultValue = "1") int page,
                                                        HttpServletRequest httpRequest){

        usuarioService.validarGestor(authService.recuperarUsuarioAutenticado(httpRequest));

        Page<FacturaEntity> historial = facturaService.recuperarHistorial(page);

        return new FacturaHistorialPageResponse(
                historial.stream()
                        .map(factura -> FacturaHistorialResponse.desdeModelo(
                                factura, facturaService.tieneFotoAsociada(factura.getId())))
                        .collect(Collectors.toList()),
                historial.getPageable().getPageNumber() + 1,
                historial.getTotalPages()
        );
    }

    @GetMapping("/{facturaId}/foto")
    public ResponseEntity<byte[]> obtenerFoto(@PathVariable UUID facturaId, HttpServletRequest httpRequest){

        usuarioService.validarGestor(authService.recuperarUsuarioAutenticado(httpRequest));

        FacturaFotoEntity foto = facturaService.obtenerFoto(facturaId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(foto.getTipoContenido()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + foto.getNombreArchivo() + "\"")
                .body(foto.getContenido());
    }

    @PutMapping("/{facturaId}/foto")
    public ResponseEntity<Void> actualizarFoto(@PathVariable UUID facturaId,
                                                @RequestParam(value = "foto", required = false) MultipartFile foto,
                                                HttpServletRequest httpRequest){

        usuarioService.validarGestor(authService.recuperarUsuarioAutenticado(httpRequest));

        facturaService.actualizarFoto(facturaId, foto);

        return ResponseEntity.ok().build();
    }
}
