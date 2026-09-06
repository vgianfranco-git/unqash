package ar.edu.unq.unqash.persistencia;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TipoCobroResponse(
        @JsonProperty("C_ID") UUID cId,
        @JsonProperty("D_DESCRIPCION") String descripcion
) {
}
