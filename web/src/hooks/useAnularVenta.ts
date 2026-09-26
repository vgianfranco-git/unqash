import { useState } from 'react'
import { ventaService } from '../services/ventaService'
import { notificationService, handleApiError } from '../services/notifications'

export function useAnularVenta(onAnulada: () => Promise<void>) {
  const [anulandoId, setAnulandoId] = useState<string | null>(null)

  const anular = async (id: string) => {
    setAnulandoId(id)
    try {
      await ventaService.anular(id)
      await onAnulada()
      notificationService.success('Operación anulada con éxito.')
    } catch (error) {
      notificationService.error(handleApiError(error, 'No se pudo anular la operación.'))
    } finally {
      setAnulandoId(null)
    }
  }

  return { anulandoId, anular }
}
