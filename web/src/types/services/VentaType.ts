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
