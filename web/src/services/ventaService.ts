import { api } from './api'
import type { VentaRequestType, VentaType } from '../types/services/VentaType'

export const ventaService = {
  crear: async (venta: VentaRequestType): Promise<VentaType> => {
    const { data } = await api.post<VentaType>('/ventas', venta)
    return data
  },
  anular: async (id: string): Promise<void> => {
    await api.post(`/ventas/${id}/anular`)
  },
}
