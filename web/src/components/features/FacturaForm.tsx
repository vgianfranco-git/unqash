import { useState } from 'react'
import Card from '../ui/Card'
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
import type { FacturaFormProps } from '../../types/components/FacturaFormProps'

const hoy = new Date().toISOString().split('T')[0]

const initialForm: FacturaRequestType = {
  proveedor: '',
  monto: undefined as unknown as number,
  fecha: hoy,
  detalles: '',
}

function FacturaForm({ onFacturaRegistrada }: FacturaFormProps) {
  const [form, setForm] = useState<FacturaRequestType>(initialForm)
  const [archivo, setArchivo] = useState<File | null>(null)
  const [errors, setErrors] = useState<FacturaFormErrors>({})
  const [isSubmitting, setIsSubmitting] = useState(false)

  const handleArchivoChange = (nuevoArchivo: File | null) => {
    setArchivo(nuevoArchivo)
    setErrors({ ...errors, archivo: validarArchivoFactura(nuevoArchivo) })
  }

  const handleSubmit = async () => {
    const nuevosErrores = validarFactura(form, archivo)
    setErrors(nuevosErrores)
    if (Object.keys(nuevosErrores).length > 0) return

    setIsSubmitting(true)
    try {
      await facturaService.crear(
        {
          ...form,
          proveedor: form.proveedor.trim(),
          fecha: formatFechaParaBackend(form.fecha),
          detalles: form.detalles?.trim() || undefined,
        },
        archivo,
      )
      notificationService.success('Factura guardada con éxito.')
      setForm(initialForm)
      setArchivo(null)
      setErrors({})
      onFacturaRegistrada?.()
    } catch (error) {
      notificationService.error(handleApiError(error, 'No se pudo guardar la factura.'))
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <Card>
      <h2 className="mb-1 text-lg font-bold text-gray-800">Nueva factura</h2>
      <p className="mb-4 text-sm text-gray-500">Facturas de compras a mayoristas o proveedores.</p>

      <div className="grid gap-4 sm:grid-cols-2 sm:gap-6">
        <div className="flex flex-col gap-4">
          <TextInput
            id="proveedor"
            label="Proveedor"
            placeholder="Ej. Mayorista Central"
            value={form.proveedor}
            disabled={isSubmitting}
            error={errors.proveedor}
            onChange={(e) => setForm({ ...form, proveedor: e.target.value })}
          />

          <div className="grid grid-cols-2 gap-4">
            <MoneyInput
              id="monto"
              label="Monto"
              value={form.monto}
              disabled={isSubmitting}
              error={errors.monto}
              onValueChange={(value) => setForm({ ...form, monto: value as number })}
            />
            <TextInput
              id="fecha"
              label="Fecha"
              type="date"
              value={form.fecha}
              disabled={isSubmitting}
              error={errors.fecha}
              onChange={(e) => setForm({ ...form, fecha: e.target.value })}
            />
          </div>

          <TextInput
            id="detalles"
            label="Detalle (opcional)"
            placeholder="Ej. Bebidas y descartables"
            value={form.detalles ?? ''}
            disabled={isSubmitting}
            error={errors.detalles}
            onChange={(e) => setForm({ ...form, detalles: e.target.value })}
          />
        </div>

        <FileDropzone
          id="archivo"
          label="Foto o archivo de la factura"
          accept={ACCEPT_ARCHIVO_FACTURA}
          hint="JPG, PNG o PDF"
          file={archivo}
          error={errors.archivo}
          disabled={isSubmitting}
          onFileChange={handleArchivoChange}
        />
      </div>

      <Button
        className="mt-4"
        loading={isSubmitting}
        loadingText="Guardando..."
        disabled={!!errors.archivo}
        onClick={handleSubmit}
      >
        Guardar factura
      </Button>
    </Card>
  )
}

export default FacturaForm
