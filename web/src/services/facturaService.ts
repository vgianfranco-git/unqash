import { api } from './api'
import type {
  FacturaDetalleType,
  FacturaHistorialPageType,
  FacturaRequestType,
  FacturaType,
} from '../types/services/FacturaType'

function buildFacturaFormData(factura: FacturaRequestType, archivo: File | null, quitarFoto?: boolean): FormData {
  const formData = new FormData()
  // El backend bindea FacturaRequest con @ModelAttribute: espera cada campo
  // suelto en el multipart (proveedor, monto, fecha, detalles), NO un blob
  // JSON anidado bajo una clave "factura" (eso solo funciona con @RequestPart).
  formData.append('proveedor', factura.proveedor)
  formData.append('monto', String(factura.monto))
  formData.append('fecha', factura.fecha)
  if (factura.detalles) formData.append('detalles', factura.detalles)
  // El backend espera el parámetro "foto" (antes se mandaba como "archivo" y nunca llegaba).
  if (archivo) formData.append('foto', archivo)
  if (quitarFoto) formData.append('quitarFoto', 'true')
  return formData
}

export const facturaService = {
  crear: async (factura: FacturaRequestType, archivo: File | null = null): Promise<FacturaType> => {
    const { data } = await api.post<FacturaType>('/facturas', buildFacturaFormData(factura, archivo))
    return data
  },
  listar: async (page: number): Promise<FacturaHistorialPageType> => {
    const { data } = await api.get<FacturaHistorialPageType>('/facturas', { params: { page } })
    return { ...data, facturas: data.facturas.filter((factura) => factura.estado !== 'ANULADA') }
  },
  anular: async (id: string): Promise<void> => {
    await api.put(`/facturas/${id}/anular`)
  },
  obtener: async (id: string): Promise<FacturaDetalleType> => {
    const { data } = await api.get<FacturaDetalleType>(`/facturas/${id}`)
    return data
  },
  editar: async (
      id: string,
      factura: FacturaRequestType,
      archivo: File | null = null,
      quitarFoto = false,
  ): Promise<FacturaType> => {
    const { data } = await api.put<FacturaType>(`/facturas/${id}`, buildFacturaFormData(factura, archivo, quitarFoto))
    return data
  },
  obtenerFoto: async (id: string): Promise<Blob> => {
    const { data } = await api.get<Blob>(`/facturas/${id}/foto`, { responseType: 'blob' })
    return data
  },
}
