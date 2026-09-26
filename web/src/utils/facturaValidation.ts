import type { FacturaFormErrors } from '../types/forms/FacturaFormErrors'
import type { FacturaRequestType } from '../types/services/FacturaType'

const MAX_MONTO = 99_999_999
const MAX_PROVEEDOR = 100
const MAX_DETALLE = 200

export function validarFactura(factura: FacturaRequestType): FacturaFormErrors {
  const errores: FacturaFormErrors = {}
  const proveedor = factura.proveedor.trim()

  if (!proveedor) errores.proveedor = 'Completá el proveedor.'
  else if (proveedor.length > MAX_PROVEEDOR)
    errores.proveedor = `El proveedor debe tener como máximo ${MAX_PROVEEDOR} caracteres.`

  if (!factura.monto || factura.monto <= 0) errores.monto = 'Ingresá un monto válido.'
  else if (factura.monto > MAX_MONTO)
    errores.monto = `El monto máximo es $${MAX_MONTO.toLocaleString('es-AR')}.`

  if (!factura.fecha) errores.fecha = 'Completá la fecha.'

  const detalle = factura.detalle?.trim()
  if (detalle && detalle.length > MAX_DETALLE)
    errores.detalle = `El detalle debe tener como máximo ${MAX_DETALLE} caracteres.`

  return errores
}
