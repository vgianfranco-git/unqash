package ar.edu.unq.unqash.factura;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

public record FacturaRequest(
        @NotNull @Size(max = 100) String proveedor,
        @NotNull @DecimalMax("99999999") BigDecimal monto,
        @NotNull String fecha,
        @Size(max = 200) String  detalles ) {
}
