package ar.edu.unq.unqash.venta;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class VentaService {

    public Venta registrar(VentaRequest request) {
        validarCamposObligatorios(request);

        return new Venta(
                UUID.randomUUID().toString(),
                request.producto(),
                request.monto(),
                request.cantidad(),
                request.monto()
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
    }
}
