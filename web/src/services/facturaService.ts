import { api } from './api'
import type { FacturaRequestType, FacturaType } from '../types/services/FacturaType'

export const facturaService = {
  crear: async (factura: FacturaRequestType): Promise<FacturaType> => {
    const { data } = await api.post<FacturaType>('/facturas', factura)
    return data
  },
}
