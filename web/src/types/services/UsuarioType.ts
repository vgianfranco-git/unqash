export interface UsuarioRequestType {
  apellido: string
  nombre: string
  dni: string
  email: string
  telefono: string
  contrasena: string
  esGestor: boolean
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
  esGestor: boolean
}

export interface UsuarioHistorialType {
  id: string
  apellido: string
  nombre: string
  dni: string
  telefono: string
  email: string
  esGestor: boolean
}

export interface UsuarioHistorialPageType {
  usuarios: UsuarioHistorialType[]
  numPag: number
  totalPag: number
  totalRegistros: number
}

export interface UsuarioEditRequestType {
  apellido: string
  nombre: string
  dni: string
  email: string
  telefono: string
  contrasena: string
  esGestor: boolean
}
