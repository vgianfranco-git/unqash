package ar.edu.unq.unqash.venta;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Venta(
        @JsonProperty("C_ID") String cId,
        String producto,
        BigDecimal monto,
        Integer cantidad,
        BigDecimal total
) {
}
