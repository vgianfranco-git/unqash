package ar.edu.unq.unqash.factura;

import ar.edu.unq.unqash.persistencia.FacturaEntity;
import ar.edu.unq.unqash.persistencia.FacturaRepository;
import ar.edu.unq.unqash.persistencia.UsuarioEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class FacturaService {

    FacturaRepository facturaRepository;

    public FacturaService(FacturaRepository facturaRepository) {
        this.facturaRepository = facturaRepository;
    }

    public FacturaEntity addFactura(FacturaRequest requestF, UsuarioEntity usuario) {
        validarDatosFactura(requestF);

        DateTimeFormatter formatter  = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate fechaFormat = LocalDate.parse(requestF.fecha(), formatter);

        UUID facturaId = UUID.randomUUID();
        return facturaRepository.save(new FacturaEntity(
                facturaId,
                requestF.proveedor(),
                requestF.monto(),
                fechaFormat,
                requestF.detalles(),
                usuario
        ));
    }

    private void validarDatosFactura(FacturaRequest requestF) {
        if(requestF.proveedor() == null || requestF.proveedor().isBlank()) {
            throw new IllegalArgumentException("proveedor obligatorio");
        }
        if(requestF.monto() == null) {
            throw new IllegalArgumentException("monto obligatorio");
        }
        if(requestF.fecha() == null) {
            throw new IllegalArgumentException("fecha obligatorio");
        }
        if(requestF.detalles() != null && requestF.detalles().isBlank()) {
            throw new IllegalArgumentException("detalle debe tener al menos un caracter");
        }
    }
}
