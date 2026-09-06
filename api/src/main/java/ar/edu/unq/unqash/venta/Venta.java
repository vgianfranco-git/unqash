package ar.edu.unq.unqash.venta;

import java.math.BigDecimal;
import java.util.UUID;

public record Venta(
        String id,
        String producto,
        BigDecimal monto,
        Integer cantidad,
        BigDecimal total,
        UUID idTipoCobro
) {
}
