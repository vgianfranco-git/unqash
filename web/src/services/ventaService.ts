import { api } from './api'
import type { VentaHistorialPageType, VentaRequestType, VentaType } from '../types/services/VentaType'

export const ventaService = {
  crear: async (venta: VentaRequestType): Promise<VentaType> => {
    const { data } = await api.post<VentaType>('/ventas', venta)
    return data
  },
  listar: async (page: number): Promise<VentaHistorialPageType> => {
    const { data } = await api.get<VentaHistorialPageType>('/ventas', { params: { page } })
    return data
  },
  anular: async (id: string): Promise<void> => {
    await api.post(`/ventas/${id}/anular`)
  },
}
