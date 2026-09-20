export interface VentaRequestType {
  producto: string
  monto: number
  cantidad: number
  idTipoCobro: string
}

export interface UsuarioVentaType {
  id: string
  nombre: string
  apellido: string
}

export interface VentaType {
  id: string
  producto: string
  monto: number
  cantidad: number
  total: number
  idTipoCobro: string
  usuario: UsuarioVentaType
}

export interface VentaHistorialType {
  id: string
  producto: string
  monto: number
  tipoCobro: string
  fechaHora: string
  idAnulada: string | null
  usuario: UsuarioVentaType
}

export interface VentaHistorialPageType {
  ventas: VentaHistorialType[]
  numPag: number
  totalPag: number
}
