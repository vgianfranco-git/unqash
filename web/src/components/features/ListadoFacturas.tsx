import { useEffect, useState } from 'react'
import FacturaCard from './FacturaCard'
import Pagination from '../ui/Pagination'
import { facturaService } from '../../services/facturaService'
import { notificationService, handleApiError } from '../../services/notifications'
import type { FacturaHistorialType } from '../../types/services/FacturaType'
import type { ListadoFacturasProps } from '../../types/components/ListadoFacturasProps'

function ListadoFacturas({ refreshKey }: ListadoFacturasProps) {
  const [page, setPage] = useState(1)
  const [facturas, setFacturas] = useState<FacturaHistorialType[]>([])
  const [totalPages, setTotalPages] = useState(1)
  const [cargando, setCargando] = useState(true)

  useEffect(() => {
    setCargando(true)
    facturaService
      .listar(page)
      .then((respuesta) => {
        setFacturas(respuesta.facturas)
        setTotalPages(respuesta.totalPag)
      })
      .catch((error) => notificationService.error(handleApiError(error, 'No se pudo cargar el historial de facturas.')))
      .finally(() => setCargando(false))
  }, [page, refreshKey])

  return (
    <div className="flex flex-col gap-3">
      <h3 className="text-sm font-semibold uppercase text-gray-500">Historial de facturas</h3>

      {cargando ? (
        <p className="text-sm text-gray-500">Cargando...</p>
      ) : facturas.length === 0 ? (
        <p className="text-center text-sm text-gray-500">Todavía no hay facturas registradas.</p>
      ) : (
        facturas.map((factura) => (
          <FacturaCard
            key={factura.id}
            proveedor={factura.proveedor}
            detalles={factura.detalles}
            fecha={factura.fecha}
            usuario={`${factura.usuario.nombre} ${factura.usuario.apellido}`}
            monto={factura.monto}
          />
        ))
      )}

      {totalPages > 1 && <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />}
    </div>
  )
}

export default ListadoFacturas
