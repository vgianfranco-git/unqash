import type { UsuarioVentaType } from './VentaType'

export interface FacturaRequestType {
  proveedor: string
  monto: number
  fecha: string
  detalles?: string
}

export interface FacturaType {
  id: string
  proveedor: string
  monto: number
  fecha: string
  detalles?: string
  fechaAlta: string
  usuarioId: string
}

export interface FacturaHistorialType {
  id: string
  proveedor: string
  monto: number
  fecha: string
  detalles: string | null
  fechaAlta: string
  usuario: UsuarioVentaType
}

export interface FacturaHistorialPageType {
  facturas: FacturaHistorialType[]
  numPag: number
  totalPag: number
}
