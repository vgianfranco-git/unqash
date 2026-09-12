import { useEffect, useState } from 'react'
import OperacionCard from './OperacionCard'
import Pagination from '../ui/Pagination'
import { ventaService } from '../../services/ventaService'
import { notificationService, handleApiError } from '../../services/notifications'
import type { VentaHistorialType } from '../../types/services/VentaType'
import type { ListadoCargasProps } from '../../types/components/ListadoCargasProps'

function ListadoCargas({ refreshKey }: ListadoCargasProps) {
  const [page, setPage] = useState(1)
  const [ventas, setVentas] = useState<VentaHistorialType[]>([])
  const [totalPages, setTotalPages] = useState(1)
  const [cargando, setCargando] = useState(true)
  const [anulandoId, setAnulandoId] = useState<string | null>(null)

  const cargarPagina = () => {
    setCargando(true)
    return ventaService
      .listar(page)
      .then((respuesta) => {
        setVentas(respuesta.ventas)
        setTotalPages(respuesta.totalPag)
      })
      .catch((error) => notificationService.error(handleApiError(error, 'No se pudo cargar el historial de ventas.')))
      .finally(() => setCargando(false))
  }

  useEffect(() => {
    cargarPagina()
  }, [page, refreshKey])

  const handleAnular = async (id: string) => {
    setAnulandoId(id)
    try {
      await ventaService.anular(id)
      await cargarPagina()
      notificationService.success('Operación anulada con éxito.')
    } catch (error) {
      notificationService.error(handleApiError(error, 'No se pudo anular la operación.'))
    } finally {
      setAnulandoId(null)
    }
  }

  return (
    <div className="flex flex-col gap-3">
      <h3 className="text-sm font-semibold uppercase text-gray-500">Tus últimas cargas</h3>

      {cargando ? (
        <p className="text-sm text-gray-500">Cargando...</p>
      ) : ventas.length === 0 ? (
        <p className="text-center text-sm text-gray-500">Todavía no registraste ninguna operación.</p>
      ) : (
        ventas.map((venta) => (
          <OperacionCard
            key={venta.id}
            producto={venta.producto}
            fecha={venta.fechaHora}
            tipoCobro={venta.tipoCobro}
            monto={Math.abs(venta.monto)}
            tipo={venta.monto < 0 ? 'anulacion' : 'ingreso'}
            puedeAnular={venta.monto > 0 && venta.idAnulada === null}
            anulando={anulandoId === venta.id}
            onAnular={() => handleAnular(venta.id)}
          />
        ))
      )}

      {totalPages > 1 && <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />}
    </div>
  )
}

export default ListadoCargas
