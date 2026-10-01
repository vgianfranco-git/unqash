package ar.edu.unq.unqash.factura;

import ar.edu.unq.unqash.persistencia.FacturaEntity;
import ar.edu.unq.unqash.persistencia.FacturaFotoEntity;
import ar.edu.unq.unqash.persistencia.FacturaFotoRepository;
import ar.edu.unq.unqash.persistencia.FacturaRepository;
import ar.edu.unq.unqash.persistencia.UsuarioEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
public class FacturaService {

    private static final Map<String, String> TIPOS_CONTENIDO_FOTO_PERMITIDOS = Map.of(
            "jpg", "image/jpeg",
            "jpeg", "image/jpeg",
            "png", "image/png",
            "pdf", "application/pdf"
    );

    private final FacturaRepository facturaRepository;
    private final FacturaFotoRepository facturaFotoRepository;

    public FacturaService(FacturaRepository facturaRepository, FacturaFotoRepository facturaFotoRepository) {
        this.facturaRepository = facturaRepository;
        this.facturaFotoRepository = facturaFotoRepository;
    }

    @Transactional
    public FacturaEntity addFactura(FacturaRequest requestF, MultipartFile foto, UsuarioEntity usuario) {
        validarDatosFactura(requestF);

        String nombreArchivoFoto = null;
        String tipoContenidoFoto = null;
        byte[] contenidoFoto = null;

        if (foto != null && !foto.isEmpty()) {
            tipoContenidoFoto = resolverTipoContenidoFoto(foto);
            nombreArchivoFoto = foto.getOriginalFilename();
            try {
                contenidoFoto = foto.getBytes();
            } catch (IOException excepcion) {
                throw new IllegalArgumentException("no se pudo leer el archivo adjunto");
            }
        }

        DateTimeFormatter formatter  = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate fechaFormat = LocalDate.parse(requestF.fecha(), formatter);

        UUID facturaId = UUID.randomUUID();
        FacturaEntity factura = facturaRepository.save(new FacturaEntity(
                facturaId,
                requestF.proveedor(),
                requestF.monto(),
                fechaFormat,
                requestF.detalles(),
                usuario
        ));

        if (contenidoFoto != null) {
            facturaFotoRepository.save(new FacturaFotoEntity(
                    UUID.randomUUID(),
                    factura,
                    nombreArchivoFoto,
                    tipoContenidoFoto,
                    contenidoFoto
            ));
        }

        return factura;
    }

    private String resolverTipoContenidoFoto(MultipartFile foto) {
        String extension = extraerExtension(foto.getOriginalFilename());
        String tipoContenido = TIPOS_CONTENIDO_FOTO_PERMITIDOS.get(extension);
        if (tipoContenido == null) {
            throw new IllegalArgumentException("formato de archivo no soportado: se aceptan JPG, JPEG, PNG o PDF");
        }
        return tipoContenido;
    }

    private String extraerExtension(String nombreArchivo) {
        if (nombreArchivo == null || !nombreArchivo.contains(".")) {
            return "";
        }
        return nombreArchivo.substring(nombreArchivo.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
    }

    private void validarDatosFactura(FacturaRequest requestF) {
        if(requestF.proveedor() == null || requestF.proveedor().isBlank()) {
            throw new IllegalArgumentException("proveedor obligatorio");
        }
        if(requestF.monto() == null) {
            throw new IllegalArgumentException("monto obligatorio");
        }
        if(requestF.fecha() == null) {
            throw new IllegalArgumentException("fecha obligatorio");
        }
        if(requestF.detalles() != null && requestF.detalles().isBlank()) {
            throw new IllegalArgumentException("detalle debe tener al menos un caracter");
        }
    }
}
