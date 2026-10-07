export interface UsuarioCardProps {
  id: string
  apellido: string
  nombre: string
  dni: string
  telefono: string
  email: string
  onEditar: (id: string) => void
}
