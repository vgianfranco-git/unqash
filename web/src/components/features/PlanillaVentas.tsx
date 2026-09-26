import { useEffect, useState } from 'react'
import Card from '../ui/Card'
import Pagination from '../ui/Pagination'
import { ventaService } from '../../services/ventaService'
import { notificationService, handleApiError } from '../../services/notifications'
import { useAnularVenta } from '../../hooks/useAnularVenta'
import { formatCurrency } from '../../utils/currency'
import type { VentaHistorialType } from '../../types/services/VentaType'

const CABECERAS = ['Fecha', 'Persona', 'Producto', 'Tipo', 'Monto', 'Acción']

function PlanillaVentas() {
  const [page, setPage] = useState(1)
  const [ventas, setVentas] = useState<VentaHistorialType[]>([])
  const [totalPages, setTotalPages] = useState(1)
  const [cargando, setCargando] = useState(true)

  const cargarPagina = (paginaActual: number) => {
    setCargando(true)
    return ventaService
      .listarTodas(paginaActual)
      .then((respuesta) => {
        setVentas(respuesta.ventas)
        setTotalPages(respuesta.totalPag)
      })
      .catch((error) => notificationService.error(handleApiError(error, 'No se pudo cargar el historial de ventas.')))
      .finally(() => setCargando(false))
  }

  useEffect(() => {
    cargarPagina(page)
  }, [page])

  const { anulandoId, anular } = useAnularVenta(() => cargarPagina(page))

  return (
    <Card>
      <div className="mb-4">
        <h2 className="text-lg font-bold text-gray-800">Planilla de ventas</h2>
        <p className="mt-1 text-sm text-gray-500">
          Registro fiel del flujo de caja.
        </p>
      </div>

      <div className="overflow-x-auto rounded-xl border border-green-light">
        <table className="w-full text-sm">
          <thead>
            <tr className="bg-green-light">
              {CABECERAS.map((cabecera) => (
                <th
                  key={cabecera}
                  className="px-3 py-2 text-left text-xs font-semibold uppercase text-gray-500"
                >
                  {cabecera}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {cargando ? (
              <tr>
                <td colSpan={6} className="py-8 text-center text-sm text-gray-500">
                  Cargando...
                </td>
              </tr>
            ) : ventas.length === 0 ? (
              <tr>
                <td colSpan={6} className="py-8 text-center text-sm text-gray-500">
                  No hay operaciones registradas.
                </td>
              </tr>
            ) : (
              ventas.map((venta) => {
                const esAnulacion = venta.monto < 0
                const estaAnulada = venta.idAnulada !== null && !esAnulacion
                const puedeAnular = !esAnulacion && !estaAnulada

                return (
                  <tr key={venta.id} className="border-b border-green-light last:border-0">
                    <td className="px-3 py-3 text-gray-500">{venta.fechaHora}</td>
                    <td className="px-3 py-3 text-gray-800">
                      {venta.usuario.nombre} {venta.usuario.apellido}
                    </td>
                    <td className="px-3 py-3 text-gray-800">{venta.producto}</td>
                    <td className="px-3 py-3 text-gray-500">{venta.tipoCobro}</td>
                    <td className={`px-3 py-3 font-semibold ${esAnulacion ? 'text-red-500' : 'text-gray-800'}`}>
                      {esAnulacion ? '-' : ''}
                      {formatCurrency(Math.abs(venta.monto))}
                    </td>
                    <td className="px-3 py-3">
                      {esAnulacion ? (
                        <span className="text-gray-400">Anulación</span>
                      ) : !puedeAnular ? (
                        <span className="text-gray-400">Anulado</span>
                      ) : (
                        <button
                          type="button"
                          disabled={anulandoId === venta.id}
                          onClick={() => anular(venta.id)}
                          className="rounded-full border border-gray-200 px-3 py-1 text-sm text-gray-600 transition-colors hover:bg-gray-50 disabled:cursor-not-allowed disabled:opacity-60"
                        >
                          {anulandoId === venta.id ? 'Anulando...' : 'Anular'}
                        </button>
                      )}
                    </td>
                  </tr>
                )
              })
            )}
          </tbody>
        </table>
      </div>

      {totalPages > 1 && (
        <div className="mt-4">
          <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />
        </div>
      )}
    </Card>
  )
}

export default PlanillaVentas
