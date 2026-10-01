package ar.edu.unq.unqash.factura;

import ar.edu.unq.unqash.auth.AuthService;
import ar.edu.unq.unqash.persistencia.FacturaEntity;
import ar.edu.unq.unqash.persistencia.UsuarioEntity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/facturas")
public class FacturaController {

    FacturaService facturaService;
    AuthService authService;

    public FacturaController(FacturaService facturaService, AuthService authService){
        this.facturaService = facturaService;
        this.authService = authService;
    }


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FacturaResponse> registrarFactura(@Valid @ModelAttribute FacturaRequest requestF,
                                                           @RequestParam(value = "foto", required = false) MultipartFile foto,
                                                           HttpServletRequest httpRequest){

        UsuarioEntity usuario = authService.recuperarUsuarioAutenticado(httpRequest);
        FacturaEntity factura = facturaService.addFactura(requestF, foto, usuario);

        return ResponseEntity.status(HttpStatus.CREATED).body(FacturaResponse.desdeModelo(factura));

    }
}
