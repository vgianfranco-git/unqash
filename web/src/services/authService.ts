import { api } from './api.ts'
import type { AuthRequestType } from '../types/services/AuthType'
import type { UsuarioType } from '../types/services/UsuarioType'

interface AuthHttpClient {
  post: <T>(ruta: string, cuerpo: AuthRequestType) => Promise<{ data: T }>
  get: <T>(ruta: string) => Promise<{ data: T }>
}

export function createAuthService(client: AuthHttpClient) {
  return {
    iniciarSesion: async (credenciales: AuthRequestType): Promise<UsuarioType> => {
      const { data } = await client.post<UsuarioType>('/auth/login', credenciales)
      return data
    },
    recuperarSesion: async (): Promise<UsuarioType> => {
      const { data } = await client.get<UsuarioType>('/auth/sesion')
      return data
    },
  }
}

export const authService = createAuthService(api)
