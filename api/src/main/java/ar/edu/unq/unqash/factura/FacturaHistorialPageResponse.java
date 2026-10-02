package ar.edu.unq.unqash.factura;

import java.util.List;

public record FacturaHistorialPageResponse(
        List<FacturaHistorialResponse> facturas,
        int numPag,
        int totalPag
) {
}
