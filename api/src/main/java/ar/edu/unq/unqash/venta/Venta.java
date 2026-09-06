package ar.edu.unq.unqash.venta;

import java.math.BigDecimal;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Venta(
        @JsonProperty("C_ID") String cId,
        String producto,
        BigDecimal monto,
        Integer cantidad,
        BigDecimal total,
        @JsonProperty("C_ID_TIPO_COBRO") UUID cIdTipoCobro
) {
}
