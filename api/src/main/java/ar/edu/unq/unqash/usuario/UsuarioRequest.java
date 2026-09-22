package ar.edu.unq.unqash.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UsuarioRequest(
        @NotBlank(message = "apellido es obligatorio")
        @Pattern(regexp = "^[\\p{L} ]+$", message = "apellido debe contener solo letras")
        @Size(max = 50, message = "apellido debe tener como máximo 50 caracteres")
        String apellido,

        @NotBlank(message = "nombre es obligatorio")
        @Pattern(regexp = "^[\\p{L} ]+$", message = "nombre debe contener solo letras")
        @Size(max = 50, message = "nombre debe tener como máximo 50 caracteres")
        String nombre,

        @NotBlank(message = "DNI es obligatorio")
        @Pattern(regexp = "^\\d{7,8}$", message = "DNI debe contener entre 7 y 8 dígitos")
        String dni,

        @NotBlank(message = "email es obligatorio")
        @Email(message = "email debe tener un formato válido")
        @Size(max = 50, message = "email debe tener como máximo 50 caracteres")
        String email,

        @NotBlank(message = "teléfono es obligatorio")
        @Pattern(regexp = "^\\d{8,15}$", message = "teléfono debe contener entre 8 y 15 dígitos")
        String telefono,

        @NotBlank(message = "contraseña es obligatoria")
        @Pattern(
                regexp = "^(?=.{10,30}$)(?=.*[A-Z])(?=.*[^A-Za-z0-9]).*$",
                message = "contraseña debe tener entre 10 y 30 caracteres, una mayúscula y un símbolo"
        )
        String contrasena,

        Boolean esGestor
) {
        public UsuarioRequest {
                if (esGestor == null){
                        esGestor = false;
                }
        }
}
