import type { FacturaFormErrors } from '../types/forms/FacturaFormErrors'
import type { FacturaRequestType } from '../types/services/FacturaType'

const MAX_MONTO = 99_999_999
const MAX_PROVEEDOR = 100
const MAX_DETALLES = 200

export function validarFactura(factura: FacturaRequestType): FacturaFormErrors {
  const errores: FacturaFormErrors = {}
  const proveedor = factura.proveedor.trim()

  if (!proveedor) errores.proveedor = 'Completá el proveedor.'
  else if (proveedor.length > MAX_PROVEEDOR)
    errores.proveedor = `El proveedor debe tener como máximo ${MAX_PROVEEDOR} caracteres.`

  if (!factura.monto || factura.monto <= 0) errores.monto = 'Ingresá un monto válido.'
  else if (factura.monto > MAX_MONTO)
    errores.monto = 'El límite de ingreso es máximo de 8 dígitos.'

  if (!factura.fecha) errores.fecha = 'Completá la fecha.'

  const detalles = factura.detalles?.trim()
  if (detalles && detalles.length > MAX_DETALLES)
    errores.detalles = `El detalle debe tener como máximo ${MAX_DETALLES} caracteres.`

  return errores
}

/** Convierte "YYYY-MM-DD" (valor del date input) a "dd/MM/yyyy" que espera el backend */
export function formatFechaParaBackend(fecha: string): string {
  const [year, month, day] = fecha.split('-')
  return `${day}/${month}/${year}`
}
