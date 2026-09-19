package ar.edu.unq.unqash.auth;

public class SesionInactivaException extends RuntimeException {

    public SesionInactivaException() {
        super("No hay una sesión activa");
    }
}
