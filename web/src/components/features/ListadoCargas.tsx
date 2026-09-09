import { useState } from 'react'
import OperacionCard from './OperacionCard'
import Pagination from '../ui/Pagination'
import { ventaService } from '../../services/ventaService'
import { notificationService, handleApiError } from '../../services/notifications'
import type { OperacionCardProps } from '../../types/components/OperacionCardProps'

type EstadoOperacion = 'activa' | 'anulada'

type OperacionMock = Omit<OperacionCardProps, 'onAnular' | 'anulando' | 'puedeAnular'> & {
  id: string
  estado: EstadoOperacion
}

const PAGE_SIZE = 3

const operacionesIniciales: OperacionMock[] = [
  { id: '1', producto: 'Capitán del Espacio + Café', fecha: '27/8, 06:24 p. m.', tipoCobro: 'Efectivo', monto: 5000, tipo: 'ingreso', estado: 'activa' },
  { id: '2', producto: 'Don Satur + Café', fecha: '26/8, 09:31 a. m.', tipoCobro: 'Efectivo', monto: 3500, tipo: 'anulacion', estado: 'anulada' },
  { id: '3', producto: 'Don Satur + Café', fecha: '26/8, 09:27 a. m.', tipoCobro: 'Efectivo', monto: 3500, tipo: 'ingreso', estado: 'anulada' },
  { id: '4', producto: 'Medialuna x3', fecha: '25/8, 11:02 a. m.', tipoCobro: 'Transferencia', monto: 1800, tipo: 'ingreso', estado: 'activa' },
  { id: '5', producto: 'Café doble', fecha: '25/8, 10:15 a. m.', tipoCobro: 'Efectivo', monto: 900, tipo: 'ingreso', estado: 'activa' },
]

function ListadoCargas() {
  const [operaciones, setOperaciones] = useState(operacionesIniciales)
  const [page, setPage] = useState(1)
  const [anulandoId, setAnulandoId] = useState<string | null>(null)

  const totalPages = Math.max(1, Math.ceil(operaciones.length / PAGE_SIZE))
  const operacionesPagina = operaciones.slice((page - 1) * PAGE_SIZE, page * PAGE_SIZE)

  const handleAnular = async (id: string) => {
    setAnulandoId(id)
    try {
      await ventaService.anular(id)
      setOperaciones((prev) => prev.map((op) => (op.id === id ? { ...op, estado: 'anulada' } : op)))
      notificationService.success('Operación anulada.')
    } catch (error) {
      notificationService.error(handleApiError(error, 'No se pudo anular la operación.'))
    } finally {
      setAnulandoId(null)
    }
  }

  return (
    <div className="flex flex-col gap-3">
      <h3 className="text-sm font-semibold uppercase text-gray-500">Tus últimas cargas</h3>

      {operacionesPagina.length === 0 ? (
        <p className="text-sm text-gray-500">Todavía no registraste ninguna operación.</p>
      ) : (
        operacionesPagina.map(({ id, estado, ...operacion }) => (
          <OperacionCard
            key={id}
            {...operacion}
            puedeAnular={operacion.tipo === 'ingreso' && estado === 'activa'}
            anulando={anulandoId === id}
            onAnular={() => handleAnular(id)}
          />
        ))
      )}

      {totalPages > 1 && <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />}
    </div>
  )
}

export default ListadoCargas
