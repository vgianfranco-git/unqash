package ar.edu.unq.unqash.venta;

import ar.edu.unq.unqash.auth.AuthService;
import ar.edu.unq.unqash.persistencia.UsuarioEntity;
import ar.edu.unq.unqash.persistencia.VentaEntity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ventas")
public class VentaController {

    private final VentaService ventaService;
    private final AuthService authService;

    public VentaController(VentaService ventaService, AuthService authService) {
        this.ventaService = ventaService;
        this.authService = authService;
    }

    @PostMapping
    public ResponseEntity<Venta> crear(@Valid @RequestBody VentaRequest request, HttpServletRequest httpRequest) {
        UsuarioEntity usuario = authService.recuperarUsuarioAutenticado(httpRequest);
        Venta venta = ventaService.registrar(request, usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(venta);
    }


//    @GetMapping
//    public ResponseEntity<List<VentaResponse>> recuperarTodas(){
//        return ResponseEntity.status(HttpStatus.OK)
//                .body(ventaService.recuperarTodas().stream()
//                        .map(VentaResponse::desdeModelo)
//                        .collect(Collectors.toList()));
//    }


    //Paginacion
    @GetMapping()
    public VentaPageResponse ventasPorPagina(@RequestParam(defaultValue = "1") int page,
                                             HttpServletRequest httpRequest){
        UsuarioEntity usuario = authService.recuperarUsuarioAutenticado(httpRequest);

        Page<VentaEntity> currVentasPage = ventaService.recuperarTodasPagina(page, usuario.getId());

        return new VentaPageResponse(currVentasPage.stream()
                .map(VentaResponse::desdeModelo)
                .collect(Collectors.toList()), currVentasPage.getPageable().getPageNumber()+1,
                currVentasPage.getTotalPages());

    }

    @GetMapping("/historial")
    public VentaPageResponse ventaHistorial(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue =
            "10")int size){

        Page<VentaEntity> historial = ventaService.recuperarHistorial(page, size);

        return new VentaPageResponse(historial.stream()
                .map(VentaResponse::desdeModelo)
                .collect(Collectors.toList()),
                historial.getPageable().getPageNumber()+1,
                historial.getTotalPages());

    }

    @PostMapping("/{id}/anular")
    public ResponseEntity<VentaResponse> anularVenta(@PathVariable UUID id){ //Cambiar a venta
        VentaEntity ventaAnulada = ventaService.anularVenta(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(VentaResponse.desdeModelo(ventaAnulada));
    }
}
