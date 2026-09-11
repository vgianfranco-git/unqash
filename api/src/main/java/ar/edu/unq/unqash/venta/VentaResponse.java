package ar.edu.unq.unqash.venta;

import ar.edu.unq.unqash.persistencia.VentaEntity;

import java.math.BigDecimal;

import java.time.LocalDate;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;


public record VentaResponse(
        UUID id,
        String producto,
        BigDecimal monto,
        String tipoCobro,
        String fechaHora
) {

    public static VentaResponse desdeModelo(VentaEntity v) {

        DateTimeFormatter formatoFechaHora = DateTimeFormatter.ofPattern("dd/MM/yyyy, hh:mm a");

        String fechaHora = v.getFechaHora() != null ? v.getFechaHora().format(formatoFechaHora) :
                LocalDateTime.now().format(formatoFechaHora);

        return new VentaResponse(
                v.getId(),
                v.getProducto(),
                v.getMonto(),
                v.tipoDeCobro().descripcion(),
                fechaHora
        );
    }
}
