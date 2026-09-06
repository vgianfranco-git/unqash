package ar.edu.unq.unqash.venta;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record VentaRequest(
        @NotBlank @Size(max = 100) String producto,
        @NotNull @DecimalMax("99999999") BigDecimal monto,
        @NotNull @Max(9999) Integer cantidad,
        @NotNull UUID idTipoCobro
) {
}
