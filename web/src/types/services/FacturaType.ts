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
