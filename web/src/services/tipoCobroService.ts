import { api } from './api'
import type { TipoCobroType } from '../types/services/TipoCobroType'

export const tipoCobroService = {
  listar: async (): Promise<TipoCobroType[]> => {
    const { data } = await api.get<TipoCobroType[]>('/tipos-cobro')
    return data
  },
}
