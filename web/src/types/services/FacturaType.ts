export interface FacturaRequestType {
  proveedor: string
  monto: number
  fecha: string
  detalle?: string
}

export interface FacturaType {
  id: string
  proveedor: string
  monto: number
  fecha: string
  detalle?: string
  fechaHoraRegistro: string
  usuario: string
}
