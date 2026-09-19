export interface UsuarioRequestType {
  apellido: string
  nombre: string
  dni: string
  email: string
  telefono: string
  contrasena: string
}

export interface UsuarioType {
  id: string
  apellido: string
  nombre: string
  dni: string
  email: string
  telefono: string
  fechaHoraAlta: string
  usuarioDeAlta: string
}
