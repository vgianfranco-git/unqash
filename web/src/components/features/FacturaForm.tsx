import { useState } from 'react'
import Card from '../ui/Card'
import TextInput from '../ui/TextInput'
import MoneyInput from '../ui/MoneyInput'
import Button from '../ui/Button'
import { facturaService } from '../../services/facturaService'
import { notificationService, handleApiError } from '../../services/notifications'
import { validarFactura } from '../../utils/facturaValidation'
import type { FacturaFormErrors } from '../../types/forms/FacturaFormErrors'
import type { FacturaRequestType } from '../../types/services/FacturaType'

const hoy = new Date().toISOString().split('T')[0]

const initialForm: FacturaRequestType = {
  proveedor: '',
  monto: undefined as unknown as number,
  fecha: hoy,
  detalle: '',
}

function FacturaForm() {
  const [form, setForm] = useState<FacturaRequestType>(initialForm)
  const [errors, setErrors] = useState<FacturaFormErrors>({})
  const [isSubmitting, setIsSubmitting] = useState(false)

  const handleSubmit = async () => {
    const nuevosErrores = validarFactura(form)
    setErrors(nuevosErrores)
    if (Object.keys(nuevosErrores).length > 0) return

    setIsSubmitting(true)
    try {
      await facturaService.crear({
        ...form,
        proveedor: form.proveedor.trim(),
        detalle: form.detalle?.trim() || undefined,
      })
      notificationService.success('Factura guardada con éxito.')
      setForm(initialForm)
      setErrors({})
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

      <div className="flex flex-col gap-4 sm:w-1/2">
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
          id="detalle"
          label="Detalle (opcional)"
          placeholder="Ej. Bebidas y descartables"
          value={form.detalle ?? ''}
          disabled={isSubmitting}
          error={errors.detalle}
          onChange={(e) => setForm({ ...form, detalle: e.target.value })}
        />
      </div>

      <Button className="mt-4" loading={isSubmitting} loadingText="Guardando..." onClick={handleSubmit}>
        Guardar factura
      </Button>
    </Card>
  )
}

export default FacturaForm
