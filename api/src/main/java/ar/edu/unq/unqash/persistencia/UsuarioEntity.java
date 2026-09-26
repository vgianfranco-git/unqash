package ar.edu.unq.unqash.persistencia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "UNQASH_USUARIO")
public class UsuarioEntity {

    @Id
    @Column(name = "C_ID", nullable = false)
    private UUID id;

    @Column(name = "D_APELLIDO", nullable = false, length = 50)
    private String apellido;

    @Column(name = "D_NOMBRE", nullable = false, length = 50)
    private String nombre;

    @Column(name = "N_DNI", nullable = false, length = 8, unique = true)
    private String dni;

    @Column(name = "D_EMAIL", nullable = false, length = 50, unique = true)
    private String email;

    @Column(name = "N_TELEFONO", nullable = false, length = 15, unique = true)
    private String telefono;

    @Column(name = "D_CONTRASENA_HASH", nullable = false, length = 255)
    private String contrasenaHash;

    @Column(name = "FECHA_HORA_ALTA", nullable = false)
    private LocalDateTime fechaHoraAlta;

    @Column(name = "USUARIO_ALTA", nullable = false, length = 50)
    private String usuarioAlta;

    @Column(name ="B_ES_GESTOR",nullable = false)
    private Boolean esGestor;

    protected UsuarioEntity() {
    }

    public UsuarioEntity(UUID id, String apellido, String nombre, String dni, String email, String telefono,
                         String contrasenaHash, LocalDateTime fechaHoraAlta, String usuarioAlta,
                         Boolean esGestor) {
        this.id = id;
        this.apellido = apellido;
        this.nombre = nombre;
        this.dni = dni;
        this.email = email;
        this.telefono = telefono;
        this.contrasenaHash = contrasenaHash;
        this.fechaHoraAlta = fechaHoraAlta;
        this.usuarioAlta = usuarioAlta;
        this.esGestor = esGestor;
    }

    public UUID getId() {
        return id;
    }

    public String getApellido() {
        return apellido;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDni() {
        return dni;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getContrasenaHash() {
        return contrasenaHash;
    }

    public LocalDateTime getFechaHoraAlta() {
        return fechaHoraAlta;
    }

    public String getUsuarioAlta() {
        return usuarioAlta;
    }

    public Boolean getEsGestor() { return esGestor; }
}
