package ar.edu.unq.unqash.factura;

import ar.edu.unq.unqash.persistencia.FacturaEntity;
import ar.edu.unq.unqash.persistencia.UsuarioEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

public record FacturaResponse(UUID id, String proveedor, BigDecimal monto, String detalles, LocalDate fecha,
                              LocalDateTime fechaAlta, UUID usuarioId, String estado) {

    public static FacturaResponse desdeModelo(FacturaEntity factura) {
        return new FacturaResponse(
                factura.getId(),
                factura.getProveedor(),
                factura.getMonto(),
                factura.getDetalle(),
                factura.getFechaFactura(),
                factura.getFechaHotaAlta(),
                factura.getUsuario().getId(),
                factura.getEstado()
        );
    }
}
