import { api } from './api'
import type { VentaRequestType, VentaType } from '../types/services/VentaType'

export const ventaService = {
  crear: async (venta: VentaRequestType): Promise<VentaType> => {
    const { data } = await api.post<VentaType>('/ventas', venta)
    return data
  },
}
