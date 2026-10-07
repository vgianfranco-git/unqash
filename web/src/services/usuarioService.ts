import { api } from './api'
import type {
  UsuarioEditRequestType,
  UsuarioHistorialPageType,
  UsuarioHistorialType,
  UsuarioRequestType,
  UsuarioType,
} from '../types/services/UsuarioType'

export const usuarioService = {
  crear: async (usuario: UsuarioRequestType): Promise<UsuarioType> => {
    const { data } = await api.post<UsuarioType>('/usuarios', usuario)
    return data
  },
  listar: async (page: number): Promise<UsuarioHistorialPageType> => {
    const { data } = await api.get<UsuarioHistorialPageType>('/usuarios/historial', { params: { page, size: 3 } })
    return data
  },
  obtener: async (id: string): Promise<UsuarioHistorialType> => {
    const { data } = await api.get<UsuarioHistorialType>(`/usuarios/${id}`)
    return data
  },
  editar: async (id: string, usuario: UsuarioEditRequestType): Promise<UsuarioHistorialType> => {
    const { data } = await api.put<UsuarioHistorialType>(`/usuarios/${id}`, usuario)
    return data
  },
}
