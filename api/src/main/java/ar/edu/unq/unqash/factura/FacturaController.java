package ar.edu.unq.unqash.factura;

import ar.edu.unq.unqash.auth.AuthService;
import ar.edu.unq.unqash.persistencia.FacturaEntity;
import ar.edu.unq.unqash.persistencia.UsuarioEntity;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/facturas")
public class FacturaController {

    FacturaService facturaService;
    AuthService authService;

    public FacturaController(FacturaService facturaService, AuthService authService){
        this.facturaService = facturaService;
        this.authService = authService;
    }


    @PostMapping
    public ResponseEntity<FacturaResponse> registrarFactura(@RequestBody FacturaRequest requestF,
                                                           HttpServletRequest httpRequest){

        UsuarioEntity usuario = authService.recuperarUsuarioAutenticado(httpRequest);
        FacturaEntity factura = facturaService.addFactura(requestF, usuario);

        return ResponseEntity.status(HttpStatus.CREATED).body(FacturaResponse.desdeModelo(factura));

    }
}
