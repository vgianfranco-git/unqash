import { api } from './api'
import type { UsuarioRequestType, UsuarioType } from '../types/services/UsuarioType'

export const usuarioService = {
  crear: async (usuario: UsuarioRequestType): Promise<UsuarioType> => {
    const { data } = await api.post<UsuarioType>('/usuarios', usuario)
    return data
  },
}
