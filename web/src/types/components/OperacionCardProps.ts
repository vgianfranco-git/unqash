export type OperacionTipo = 'ingreso' | 'anulacion'

export interface OperacionCardProps {
  producto: string
  fecha: string
  tipoCobro: string
  monto: number
  tipo: OperacionTipo
  anulando?: boolean
  onAnular?: () => void
}
