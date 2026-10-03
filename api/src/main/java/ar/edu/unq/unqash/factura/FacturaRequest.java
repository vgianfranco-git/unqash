package ar.edu.unq.unqash.factura;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record FacturaRequest(
        @NotNull(message = "proveedor es obligatorio")
        @Size(max = 100, message = "proveedor debe tener como máximo 100 caracteres")
        String proveedor,

        @NotNull(message = "monto es obligatorio")
        @DecimalMin(value = "0", inclusive = false, message = "monto debe ser mayor a cero")
        @DecimalMax(value = "99999999", message = "monto debe ser como máximo $99.999.999")
        BigDecimal monto,

        @NotNull(message = "fecha es obligatoria")
        String fecha,

        @Size(max = 200, message = "detalle debe tener como máximo 200 caracteres")
        String detalles
) {
}
