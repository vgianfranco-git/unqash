package ar.edu.unq.unqash.venta;

import ar.edu.unq.unqash.persistencia.VentaEntity;

import java.math.BigDecimal;

import java.time.LocalDate;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


public record VentaResponse(
                               String producto,
                               BigDecimal monto,
                               String tipoCobro,
                               String fecha,
                               String hora
) {

    public static VentaResponse desdeModelo(VentaEntity v) {

        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("hh:mm a");

        String fecha = v.getFecha() != null ? v.getFecha().format(formatoFecha) :
                LocalDate.now().format(formatoFecha);

        String hora = v.getHora() != null ? v.getHora().format(formatoHora) :
                LocalTime.now().format(formatoHora);

        return new VentaResponse(
                v.getProducto(),
                v.getMonto(),
                v.tipoDeCobro().descripcion(),
                fecha,
                hora
        );
    }
}
