import type { UsuarioVentaType } from '../services/VentaType'

export type OperacionTipo = 'ingreso' | 'anulacion'

export interface OperacionCardProps {
  producto: string
  fecha: string
  tipoCobro: string
  usuario: UsuarioVentaType
  monto: number
  tipo: OperacionTipo
  puedeAnular: boolean
  anulando?: boolean
  onAnular?: () => void
}
