import { api } from './api'
import type { FacturaRequestType, FacturaType } from '../types/services/FacturaType'

export const facturaService = {
  crear: async (factura: FacturaRequestType, archivo: File | null = null): Promise<FacturaType> => {
    const formData = new FormData()
    formData.append('factura', new Blob([JSON.stringify(factura)], { type: 'application/json' }))
    if (archivo) formData.append('archivo', archivo)

    const { data } = await api.post<FacturaType>('/facturas', formData)
    return data
  },
}
