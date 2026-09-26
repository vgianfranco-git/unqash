import type { FacturaRequestType, FacturaType } from '../types/services/FacturaType'

// TODO: reemplazar por llamada real cuando el back esté listo
// const { data } = await api.post<FacturaType>('/facturas', factura)
export const facturaService = {
  crear: (_factura: FacturaRequestType): Promise<FacturaType> =>
    new Promise((resolve) =>
      setTimeout(
        () =>
          resolve({
            id: crypto.randomUUID(),
            ..._factura,
            fechaHoraRegistro: new Date().toISOString(),
            usuario: 'mock',
          }),
        500,
      ),
    ),
}
