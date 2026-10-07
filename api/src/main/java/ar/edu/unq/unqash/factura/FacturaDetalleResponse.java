package ar.edu.unq.unqash.factura;

import ar.edu.unq.unqash.persistencia.FacturaEntity;
import ar.edu.unq.unqash.persistencia.UsuarioEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record FacturaDetalleResponse(
        UUID id,
        String proveedor,
        BigDecimal monto,
        LocalDate fecha,
        String detalle,
        String usuarioCarga,
        LocalDateTime fechaAlta,
        boolean tieneFoto,
        String nombreArchivoFoto,
        String estado
) {

    public static FacturaDetalleResponse desdeModelo(FacturaEntity factura, String nombreArchivoFoto) {
        UsuarioEntity usuario = factura.getUsuario();
        return new FacturaDetalleResponse(
                factura.getId(),
                factura.getProveedor(),
                factura.getMonto(),
                factura.getFechaFactura(),
                factura.getDetalle(),
                usuario.getNombre() + " " + usuario.getApellido(),
                factura.getFechaHotaAlta(),
                nombreArchivoFoto != null,
                nombreArchivoFoto,
                factura.getEstado()
        );
    }
}
