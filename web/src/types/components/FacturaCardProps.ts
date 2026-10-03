export interface FacturaCardProps {
  id: string
  proveedor: string
  detalles: string | null
  fecha: string
  usuario: string
  monto: number
  tieneFoto: boolean
  onEditar: (id: string) => void
  onVerFoto: (id: string) => void
}
