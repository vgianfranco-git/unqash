package ar.edu.unq.unqash.venta;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VentaRequest(
        @NotBlank String producto,
        @NotNull BigDecimal monto,
        @NotNull Integer cantidad,
        @NotNull UUID idTipoCobro
) {
}
