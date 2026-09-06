import { useEffect, useState } from 'react'
import Card from '../ui/Card'
import TextInput from '../ui/TextInput'
import MoneyInput from '../ui/MoneyInput'
import Select from '../ui/Select'
import Button from '../ui/Button'
import { tipoCobroService } from '../../services/tipoCobroService'
import { ventaService } from '../../services/ventaService'
import { notificationService, handleApiError } from '../../services/notifications'
import { formatCurrency } from '../../utils/currency'
import type { TipoCobroType } from '../../types/services/TipoCobroType'
import type { VentaFormErrors } from '../../types/forms/VentaFormErrors'

const initialForm = {
  producto: '',
  monto: undefined as number | undefined,
  cantidad: '1',
  idTipoCobro: '',
}

const MAX_PRODUCTO_LENGTH = 100
const MAX_MONTO = 99_999_999
const MAX_CANTIDAD = 9999

function VentaForm() {
  const [tiposCobro, setTiposCobro] = useState<TipoCobroType[]>([])
  const [form, setForm] = useState(initialForm)
  const [errors, setErrors] = useState<VentaFormErrors>({})
  const [isSubmitting, setIsSubmitting] = useState(false)

  useEffect(() => {
    tipoCobroService
      .listar()
      .then(setTiposCobro)
      .catch((error) => notificationService.error(handleApiError(error, 'No se pudieron cargar los tipos de cobro.')))
  }, [])

  const total = form.monto ?? 0

  const validar = (): VentaFormErrors => {
    const nuevosErrores: VentaFormErrors = {}
    if (!form.producto.trim()) nuevosErrores.producto = 'Completá el producto o concepto.'
    else if (form.producto.trim().length > MAX_PRODUCTO_LENGTH)
      nuevosErrores.producto = `Máximo ${MAX_PRODUCTO_LENGTH} caracteres.`

    if (!form.monto || form.monto <= 0) nuevosErrores.monto = 'Ingresá un monto válido.'
    else if (form.monto > MAX_MONTO) nuevosErrores.monto = `El monto máximo es ${formatCurrency(MAX_MONTO)}.`

    if (!form.cantidad || Number(form.cantidad) <= 0) nuevosErrores.cantidad = 'Ingresá una cantidad válida.'
    else if (Number(form.cantidad) > MAX_CANTIDAD) nuevosErrores.cantidad = `La cantidad máxima es ${MAX_CANTIDAD}.`
    if (!form.idTipoCobro) nuevosErrores.idTipoCobro = 'Seleccioná un tipo de cobro.'
    return nuevosErrores
  }

  const handleSubmit = async () => {
    const nuevosErrores = validar()
    setErrors(nuevosErrores)
    if (Object.keys(nuevosErrores).length > 0) return

    setIsSubmitting(true)
    try {
      await ventaService.crear({
        producto: form.producto.trim(),
        monto: form.monto!,
        cantidad: Number(form.cantidad),
        idTipoCobro: form.idTipoCobro,
      })
      notificationService.success('Operación registrada con éxito.')
      setForm(initialForm)
      setErrors({})
    } catch (error) {
      notificationService.error(handleApiError(error, 'No se pudo registrar la venta.'))
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <Card>
      <h2 className="mb-4 text-lg font-bold text-gray-800">Nueva operación</h2>

      <div className="flex flex-col gap-4">
        <TextInput
          id="producto"
          label="Producto / Concepto"
          placeholder="Ej. Combo cafe + medialunas"
          maxLength={MAX_PRODUCTO_LENGTH}
          value={form.producto}
          disabled={isSubmitting}
          error={errors.producto}
          onChange={(e) => setForm({ ...form, producto: e.target.value })}
        />

        <div className="grid grid-cols-2 gap-4">
          <MoneyInput
            id="monto"
            label="Monto"
            value={form.monto}
            max={MAX_MONTO}
            disabled={isSubmitting}
            error={errors.monto}
            onValueChange={(value) => setForm({ ...form, monto: value })}
          />
          <TextInput
            id="cantidad"
            label="Cantidad"
            type="number"
            min={1}
            max={MAX_CANTIDAD}
            value={form.cantidad}
            disabled={isSubmitting}
            error={errors.cantidad}
            onChange={(e) => {
              const valor = e.target.value
              if (valor.length > String(MAX_CANTIDAD).length) return
              setForm({ ...form, cantidad: valor })
            }}
          />
        </div>

        <Select
          id="tipoCobro"
          label="Tipo de cobro"
          placeholder="Seleccioná un tipo de cobro"
          value={form.idTipoCobro}
          disabled={isSubmitting}
          error={errors.idTipoCobro}
          options={tiposCobro.map((tipo) => ({ value: tipo.id, label: tipo.descripcion }))}
          onChange={(value) => setForm({ ...form, idTipoCobro: value })}
        />

        <div className="flex items-center justify-between text-sm">
          <span className="text-gray-500">Total de la operación</span>
          <span className="font-semibold text-gray-800">{formatCurrency(total)}</span>
        </div>

        <Button loading={isSubmitting} loadingText="Registrando..." onClick={handleSubmit}>
          Registrar operación
        </Button>
      </div>
    </Card>
  )
}

export default VentaForm
