package ar.edu.unq.unqash.venta;


import java.util.List;
import java.util.UUID;

import ar.edu.unq.unqash.persistencia.TipoCobro;
import ar.edu.unq.unqash.persistencia.TipoCobroRepository;
import ar.edu.unq.unqash.persistencia.VentaEntity;
import ar.edu.unq.unqash.persistencia.VentaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final TipoCobroRepository tipoCobroRepository;

    public VentaService(VentaRepository ventaRepository, TipoCobroRepository tipoCobroRepository) {
        this.ventaRepository = ventaRepository;
        this.tipoCobroRepository = tipoCobroRepository;
    }

    public Venta registrar(VentaRequest request) {
        validarCamposObligatorios(request);
        TipoCobro tipoDeCobro = tipoCobroRepository.findById(request.idTipoCobro())
                .orElseThrow(() -> new IllegalArgumentException("tipo de cobro inexistente"));
        UUID ventaId = UUID.randomUUID();
        ventaRepository.save(new VentaEntity(
                ventaId,
                request.producto(),
                request.monto(),
                request.cantidad(),
                tipoDeCobro
        ));

        return new Venta(
                ventaId.toString(),
                request.producto(),
                request.monto(),
                request.cantidad(),
                request.monto(),
                request.idTipoCobro()
        );
    }

    private void validarCamposObligatorios(VentaRequest request) {
        if (request == null || request.producto() == null || request.producto().isBlank()) {
            throw new IllegalArgumentException("producto es obligatorio");
        }
        if (request.monto() == null) {
            throw new IllegalArgumentException("monto es obligatorio");
        }
        if (request.cantidad() == null) {
            throw new IllegalArgumentException("cantidad es obligatoria");
        }
        if (request.idTipoCobro() == null) {
            throw new IllegalArgumentException("tipo de cobro es obligatorio");
        }
    }

    public List<VentaEntity> recuperarTodas() {
        return ventaRepository.findAll();
    }

    public Page<VentaEntity> recuperarTodasPagina(int page){
        int pageSize = 3; //limit
        validarCamposPaginacion(page);

        Sort sort = Sort.by("fechaHora").descending();
        Pageable pageable = PageRequest.of(page - 1, pageSize, sort);

        return ventaRepository.findAll(pageable);
    }

    private void validarCamposPaginacion(int page) {
        if(page < 1) throw new IllegalArgumentException("El número de página debe ser mayor a 0.");
    }

}
