package ar.edu.unq.unqash.venta;

import java.util.List;

public record VentaPageResponse(
        List<VentaResponse> ventas,
        int numPag,
        int totalPag
) {


}
