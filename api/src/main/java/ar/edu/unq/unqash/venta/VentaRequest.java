package ar.edu.unq.unqash.venta;

import java.math.BigDecimal;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VentaRequest(
        @NotBlank String producto,
        @NotNull BigDecimal monto,
        @NotNull Integer cantidad,
        @NotNull @JsonProperty("C_ID_TIPO_COBRO") UUID cIdTipoCobro
) {
}
