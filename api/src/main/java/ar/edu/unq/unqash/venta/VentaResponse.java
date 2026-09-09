package ar.edu.unq.unqash.venta;

import ar.edu.unq.unqash.persistencia.VentaEntity;

import java.math.BigDecimal;

import java.time.LocalDate;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


public record VentaResponse(
                               String producto,
                               BigDecimal monto,
                               String tipoCobro,
                               String fechaHora
) {

    public static VentaResponse desdeModelo(VentaEntity v) {

//        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("hh:mm a");
//
//        String fecha = v.getFecha() != null ? v.getFecha().format(formatoFecha) :
//                LocalDate.now().format(formatoFecha);
//
//        String hora = v.getHora() != null ? v.getHora().format(formatoHora) :
//                LocalTime.now().format(formatoHora);

        DateTimeFormatter formatoFechaHora = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");

        String fechaHora = v.getFechaHora() != null ? v.getFechaHora().format(formatoFechaHora) :
                LocalDateTime.now().format(formatoFechaHora);

        return new VentaResponse(
                v.getProducto(),
                v.getMonto(),
                v.tipoDeCobro().descripcion(),
                fechaHora
        );
    }
}
