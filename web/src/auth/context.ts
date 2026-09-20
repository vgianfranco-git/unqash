import { createContext } from 'react'
import type { AuthRequestType } from '../types/services/AuthType'
import type { UsuarioType } from '../types/services/UsuarioType'

export interface AuthContextValue {
  usuario: UsuarioType | null
  cargandoSesion: boolean
  iniciarSesion: (credenciales: AuthRequestType) => Promise<void>
}

export const AuthContext = createContext<AuthContextValue | undefined>(undefined)
