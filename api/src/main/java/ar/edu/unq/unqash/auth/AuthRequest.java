package ar.edu.unq.unqash.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AuthRequest(
        @NotBlank(message = "DNI es obligatorio")
        @Pattern(regexp = "^\\d{7,8}$", message = "DNI debe contener entre 7 y 8 dígitos")
        String dni,

        @NotBlank(message = "contraseña es obligatoria")
        @Size(min = 10, max = 30, message = "contraseña debe tener entre 10 y 30 caracteres")
        String contrasena
) {
}
