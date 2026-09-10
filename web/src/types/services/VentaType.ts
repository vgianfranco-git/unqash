export interface VentaRequestType {
  producto: string
  monto: number
  cantidad: number
  idTipoCobro: string
}

export interface VentaType {
  id: string
  producto: string
  monto: number
  cantidad: number
  total: number
  idTipoCobro: string
}

export interface VentaHistorialType {
  producto: string
  monto: number
  tipoCobro: string
  fechaHora: string
}

export interface VentaHistorialPageType {
  ventas: VentaHistorialType[]
  numPag: number
  totalPag: number
}
