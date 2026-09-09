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

  useEffect(() => {
    setCargando(true)
    ventaService
      .listar(page)
      .then((respuesta) => {
        setVentas(respuesta.ventas)
        setTotalPages(respuesta.totalPag)
      })
      .catch((error) => notificationService.error(handleApiError(error, 'No se pudo cargar el historial de ventas.')))
      .finally(() => setCargando(false))
  }, [page, refreshKey])

  return (
    <div className="flex flex-col gap-3">
      <h3 className="text-sm font-semibold uppercase text-gray-500">Tus últimas cargas</h3>

      {cargando ? (
        <p className="text-sm text-gray-500">Cargando...</p>
      ) : ventas.length === 0 ? (
        <p className="text-center text-sm text-gray-500">Todavía no registraste ninguna operación.</p>
      ) : (
        ventas.map((venta, index) => (
          <OperacionCard
            key={`${venta.fechaHora}-${index}`}
            producto={venta.producto}
            fecha={venta.fechaHora}
            tipoCobro={venta.tipoCobro}
            monto={venta.monto}
            tipo="ingreso"
          />
        ))
      )}

      {totalPages > 1 && <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />}
    </div>
  )
}

export default ListadoCargas
