package ar.edu.unq.unqash.factura;

import ar.edu.unq.unqash.persistencia.FacturaEntity;
import ar.edu.unq.unqash.persistencia.FacturaFotoEntity;
import ar.edu.unq.unqash.persistencia.FacturaFotoRepository;
import ar.edu.unq.unqash.persistencia.FacturaRepository;
import ar.edu.unq.unqash.persistencia.UsuarioEntity;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
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
    private final EntityManager entityManager;

    public FacturaService(FacturaRepository facturaRepository, FacturaFotoRepository facturaFotoRepository,
                          EntityManager entityManager) {
        this.facturaRepository = facturaRepository;
        this.facturaFotoRepository = facturaFotoRepository;
        this.entityManager = entityManager;
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
                throw new IllegalArgumentException("No se pudo leer el archivo adjunto.");
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
            throw new IllegalArgumentException("Formato no soportado: Se aceptan JPG, JPEG, PNG o PDF.");
        }
        return tipoContenido;
    }

    private String extraerExtension(String nombreArchivo) {
        if (nombreArchivo == null || !nombreArchivo.contains(".")) {
            return "";
        }
        return nombreArchivo.substring(nombreArchivo.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
    }

    public Page<FacturaEntity> recuperarHistorial(int page) {
        int pageSize = 3;
        validarCamposPaginacion(page);

        Sort sort = Sort.by("fechaHotaAlta").descending();
        Pageable pageable = PageRequest.of(page - 1, pageSize, sort);

        return facturaRepository.findAll(pageable);
    }

    private void validarCamposPaginacion(int page) {
        if (page < 1) {
            throw new IllegalArgumentException("El número de página debe ser mayor a 0.");
        }
    }

    public FacturaEntity obtenerFactura(UUID facturaId) {
        return facturaRepository.findById(facturaId)
                .orElseThrow(() -> new IllegalArgumentException("Factura inexistente."));
    }

    @Transactional
    public FacturaEntity anularFactura(UUID facturaId) {
        if (facturaRepository.anularSiActiva(facturaId) == 0) {
            obtenerFactura(facturaId);
            throw new IllegalArgumentException("La factura ya está anulada.");
        }
        return obtenerFactura(facturaId);
    }

    public FacturaFotoEntity obtenerFoto(UUID facturaId) {
        return facturaFotoRepository.findByFactura_Id(facturaId)
                .orElseThrow(() -> new IllegalArgumentException("La factura no tiene una foto asociada."));
    }

    public Optional<FacturaFotoEntity> buscarFotoAsociada(UUID facturaId) {
        return facturaFotoRepository.findByFactura_Id(facturaId);
    }

    public boolean tieneFotoAsociada(UUID facturaId) {
        return facturaFotoRepository.existsByFactura_Id(facturaId);
    }

    @Transactional
    public FacturaEntity editarFactura(UUID facturaId, FacturaRequest requestF, MultipartFile foto,
                                        boolean quitarFoto, UsuarioEntity usuarioModificacion) {
        FacturaEntity factura = facturaRepository.findById(facturaId)
                .orElseThrow(() -> new IllegalArgumentException("Factura inexistente."));

        validarDatosFactura(requestF);

        boolean hayFotoNueva = foto != null && !foto.isEmpty();
        String tipoContenido = null;
        byte[] contenido = null;
        if (hayFotoNueva) {
            tipoContenido = resolverTipoContenidoFoto(foto);
            try {
                contenido = foto.getBytes();
            } catch (IOException excepcion) {
                throw new IllegalArgumentException("No se pudo leer el archivo adjunto.");
            }
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate fechaFormat = LocalDate.parse(requestF.fecha(), formatter);

        factura.actualizarDatos(requestF.proveedor(), requestF.monto(), fechaFormat, requestF.detalles(),
                usuarioModificacion);
        facturaRepository.save(factura);

        if (hayFotoNueva) {
            Optional<FacturaFotoEntity> fotoExistente = facturaFotoRepository.findByFactura_Id(facturaId);
            if (fotoExistente.isPresent()) {
                fotoExistente.get().actualizarContenido(foto.getOriginalFilename(), tipoContenido, contenido);
                facturaFotoRepository.save(fotoExistente.get());
            } else {
                facturaFotoRepository.save(new FacturaFotoEntity(
                        UUID.randomUUID(),
                        factura,
                        foto.getOriginalFilename(),
                        tipoContenido,
                        contenido
                ));
            }
        } else if (quitarFoto) {
            facturaFotoRepository.findByFactura_Id(facturaId).ifPresent(facturaFotoRepository::delete);
        }

        entityManager.flush();
        entityManager.refresh(factura);
        return factura;
    }

    private void validarDatosFactura(FacturaRequest requestF) {
        if(requestF.proveedor() == null || requestF.proveedor().isBlank()) {
            throw new IllegalArgumentException("Proveedor obligatorio.");
        }
        if(requestF.monto() == null) {
            throw new IllegalArgumentException("Monto obligatorio.");
        }
        if(requestF.fecha() == null) {
            throw new IllegalArgumentException("Fecha obligatorio.");
        }
        if(requestF.detalles() != null && requestF.detalles().isBlank()) {
            throw new IllegalArgumentException("Detalle debe tener al menos un caracter.");
        }
    }
}
