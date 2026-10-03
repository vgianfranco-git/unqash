import { useEffect, useState } from 'react'
import Modal from '../ui/Modal'
import TextInput from '../ui/TextInput'
import MoneyInput from '../ui/MoneyInput'
import FileDropzone from '../ui/FileDropzone'
import Button from '../ui/Button'
import { facturaService } from '../../services/facturaService'
import { notificationService, handleApiError } from '../../services/notifications'
import {
  validarFactura,
  validarArchivoFactura,
  formatFechaParaBackend,
  ACCEPT_ARCHIVO_FACTURA,
} from '../../utils/facturaValidation'
import type { FacturaFormErrors } from '../../types/forms/FacturaFormErrors'
import type { FacturaRequestType } from '../../types/services/FacturaType'
import type { EditarFacturaModalProps } from '../../types/components/EditarFacturaModalProps'

function formatFechaAlta(fechaAlta: string): string {
  const fecha = new Date(fechaAlta)
  if (Number.isNaN(fecha.getTime())) return fechaAlta
  const fechaTexto = fecha.toLocaleDateString('es-AR')
  const horaTexto = fecha.toLocaleTimeString('es-AR', { hour: '2-digit', minute: '2-digit' })
  return `${fechaTexto}, ${horaTexto}`
}

function EditarFacturaModal({ facturaId, onClose, onFacturaActualizada }: EditarFacturaModalProps) {
  const [cargando, setCargando] = useState(true)
  const [errorCarga, setErrorCarga] = useState<string | null>(null)
  const [subtitulo, setSubtitulo] = useState('')

  const [form, setForm] = useState<FacturaRequestType | null>(null)
  const [archivoNuevo, setArchivoNuevo] = useState<File | null>(null)
  const [nombreFotoExistente, setNombreFotoExistente] = useState<string | null>(null)
  const [teniaFotoOriginal, setTeniaFotoOriginal] = useState(false)

  const [errors, setErrors] = useState<FacturaFormErrors>({})
  const [isSubmitting, setIsSubmitting] = useState(false)

  useEffect(() => {
    facturaService
      .obtener(facturaId)
      .then((factura) => {
        setForm({
          proveedor: factura.proveedor,
          monto: factura.monto,
          fecha: factura.fecha,
          detalles: factura.detalle ?? '',
        })
        setNombreFotoExistente(factura.nombreArchivoFoto)
        setTeniaFotoOriginal(factura.tieneFoto)
        setSubtitulo(`Cargada por ${factura.usuarioCarga} · ${formatFechaAlta(factura.fechaAlta)}`)
      })
      .catch((err) => setErrorCarga(handleApiError(err, 'No se pudo cargar la factura.')))
      .finally(() => setCargando(false))
  }, [facturaId])

  const handleArchivoChange = (nuevoArchivo: File | null) => {
    setArchivoNuevo(nuevoArchivo)
    setErrors({ ...errors, archivo: validarArchivoFactura(nuevoArchivo) })
  }

  const handleSubmit = async () => {
    if (!form) return

    const nuevosErrores = validarFactura(form, archivoNuevo)
    setErrors(nuevosErrores)
    if (Object.keys(nuevosErrores).length > 0) return

    // Se quita la foto solo si tenía una originalmente, no quedó ninguna existente marcada
    // y no se seleccionó un reemplazo — tal como lo definió el back (desasociar + asociar nueva).
    const quitarFoto = teniaFotoOriginal && !nombreFotoExistente && !archivoNuevo

    setIsSubmitting(true)
    try {
      await facturaService.editar(
        facturaId,
        {
          ...form,
          proveedor: form.proveedor.trim(),
          fecha: formatFechaParaBackend(form.fecha),
          detalles: form.detalles?.trim() || undefined,
        },
        archivoNuevo,
        quitarFoto,
      )
      notificationService.success('Factura actualizada con éxito.')
      onFacturaActualizada()
      onClose()
    } catch (error) {
      notificationService.error(handleApiError(error, 'No se pudo actualizar la factura.'))
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <Modal title="Editar factura" subtitle={cargando ? undefined : subtitulo} widthClassName="max-w-xl" onClose={onClose}>
      {cargando && <p className="py-10 text-center text-sm text-gray-500">Cargando...</p>}

      {!cargando && errorCarga && <p className="py-10 text-center text-sm text-red-500">{errorCarga}</p>}

      {!cargando && !errorCarga && form && (
        <div className="flex flex-col gap-4">
          <TextInput
            id="editar-proveedor"
            label="Proveedor"
            value={form.proveedor}
            disabled={isSubmitting}
            error={errors.proveedor}
            onChange={(e) => setForm({ ...form, proveedor: e.target.value })}
          />

          <div className="grid grid-cols-2 gap-4">
            <MoneyInput
              id="editar-monto"
              label="Monto"
              value={form.monto}
              disabled={isSubmitting}
              error={errors.monto}
              onValueChange={(value) => setForm({ ...form, monto: value as number })}
            />
            <TextInput
              id="editar-fecha"
              label="Fecha"
              type="date"
              value={form.fecha}
              disabled={isSubmitting}
              error={errors.fecha}
              onChange={(e) => setForm({ ...form, fecha: e.target.value })}
            />
          </div>

          <TextInput
            id="editar-detalles"
            label="Detalle (opcional)"
            value={form.detalles ?? ''}
            disabled={isSubmitting}
            error={errors.detalles}
            onChange={(e) => setForm({ ...form, detalles: e.target.value })}
          />

          <FileDropzone
            id="editar-archivo"
            label="Foto o archivo de la factura"
            accept={ACCEPT_ARCHIVO_FACTURA}
            hint="JPG, PNG o PDF"
            file={archivoNuevo}
            existingFileName={nombreFotoExistente}
            error={errors.archivo}
            disabled={isSubmitting}
            onFileChange={handleArchivoChange}
            onRemoveExisting={() => setNombreFotoExistente(null)}
          />

          <Button
            loading={isSubmitting}
            loadingText="Guardando..."
            disabled={!!errors.archivo}
            onClick={handleSubmit}
          >
            Guardar cambios
          </Button>
        </div>
      )}
    </Modal>
  )
}

export default EditarFacturaModal
