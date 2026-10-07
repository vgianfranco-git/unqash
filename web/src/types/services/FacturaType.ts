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
  detalle: string | null
  usuarioCarga: string
  tieneFoto: boolean
  estado: string
}

export interface FacturaHistorialPageType {
  facturas: FacturaHistorialType[]
  numPag: number
  totalPag: number
}

export interface FacturaDetalleType {
  id: string
  proveedor: string
  monto: number
  fecha: string
  detalle: string | null
  usuarioCarga: string
  fechaAlta: string
  tieneFoto: boolean
  nombreArchivoFoto: string | null
}