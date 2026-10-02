package ar.edu.unq.unqash.factura;

import ar.edu.unq.unqash.persistencia.FacturaEntity;
import ar.edu.unq.unqash.persistencia.UsuarioEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record FacturaHistorialResponse(
        UUID id,
        String proveedor,
        BigDecimal monto,
        LocalDate fecha,
        String detalle,
        String usuarioCarga,
        boolean tieneFoto
) {

    public static FacturaHistorialResponse desdeModelo(FacturaEntity factura, boolean tieneFoto) {
        UsuarioEntity usuario = factura.getUsuario();
        return new FacturaHistorialResponse(
                factura.getId(),
                factura.getProveedor(),
                factura.getMonto(),
                factura.getFechaFactura(),
                factura.getDetalle(),
                usuario.getNombre() + " " + usuario.getApellido(),
                tieneFoto
        );
    }
}
