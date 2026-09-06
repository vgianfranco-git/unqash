package ar.edu.unq.unqash.persistencia;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tipos-cobro")
public class TipoCobroController {

    private final TipoCobroRepository tipoCobroRepository;

    public TipoCobroController(TipoCobroRepository tipoCobroRepository) {
        this.tipoCobroRepository = tipoCobroRepository;
    }

    @GetMapping
    public List<TipoCobroResponse> listar() {
        return tipoCobroRepository.findAllByOrderByDescripcionAsc().stream()
                .map(tipo -> new TipoCobroResponse(tipo.id(), tipo.descripcion()))
                .toList();
    }
}
