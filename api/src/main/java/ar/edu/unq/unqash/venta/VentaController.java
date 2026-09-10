package ar.edu.unq.unqash.venta;

import ar.edu.unq.unqash.persistencia.VentaEntity;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/ventas")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @PostMapping
    public ResponseEntity<Venta> crear(@Valid @RequestBody VentaRequest request) {
        Venta venta = ventaService.registrar(request);
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
    public VentaPageResponse ventasPorPagina(@RequestParam(defaultValue = "1") int page){

        Page<VentaEntity> currVentasPage = ventaService.recuperarTodasPagina(page);

        return new VentaPageResponse(currVentasPage.stream()
                .map(VentaResponse::desdeModelo)
                .collect(Collectors.toList()), currVentasPage.getPageable().getPageNumber()+1,
                currVentasPage.getTotalPages());

    }
}
