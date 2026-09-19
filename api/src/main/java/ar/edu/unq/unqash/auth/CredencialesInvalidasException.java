package ar.edu.unq.unqash.auth;

public class CredencialesInvalidasException extends RuntimeException {

    public CredencialesInvalidasException() {
        super("DNI o contraseña incorrectos");
    }
}
