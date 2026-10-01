import { api } from './api'
import type { FacturaHistorialPageType, FacturaRequestType, FacturaType } from '../types/services/FacturaType'

export const facturaService = {
  crear: async (factura: FacturaRequestType): Promise<FacturaType> => {
    const { data } = await api.post<FacturaType>('/facturas', factura)
    return data
  },
  listar: async (page: number): Promise<FacturaHistorialPageType> => {
    const { data } = await api.get<FacturaHistorialPageType>('/facturas', { params: { page } })
    return data
  },
}
