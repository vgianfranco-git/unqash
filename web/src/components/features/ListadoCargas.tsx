import { useState } from 'react'
import OperacionCard from './OperacionCard'
import Pagination from '../ui/Pagination'
import type { OperacionCardProps } from '../../types/components/OperacionCardProps'

type OperacionMock = Omit<OperacionCardProps, 'onAnular' | 'anulando'> & { id: string }

const PAGE_SIZE = 3

const mockOperaciones: OperacionMock[] = [
  { id: '1', producto: 'Capitán del Espacio + Café', fecha: '27/8, 06:24 p. m.', tipoCobro: 'Efectivo', monto: 5000, tipo: 'ingreso' },
  { id: '2', producto: 'Don Satur + Café', fecha: '26/8, 09:31 a. m.', tipoCobro: 'Efectivo', monto: 3500, tipo: 'anulacion' },
  { id: '3', producto: 'Don Satur + Café', fecha: '26/8, 09:27 a. m.', tipoCobro: 'Efectivo', monto: 3500, tipo: 'ingreso' },
  { id: '4', producto: 'Medialuna x3', fecha: '25/8, 11:02 a. m.', tipoCobro: 'Transferencia', monto: 1800, tipo: 'ingreso' },
  { id: '5', producto: 'Café doble', fecha: '25/8, 10:15 a. m.', tipoCobro: 'Efectivo', monto: 900, tipo: 'ingreso' },
]

function ListadoCargas() {
  const [page, setPage] = useState(1)
  const [anulandoId, setAnulandoId] = useState<string | null>(null)

  const totalPages = Math.max(1, Math.ceil(mockOperaciones.length / PAGE_SIZE))
  const operacionesPagina = mockOperaciones.slice((page - 1) * PAGE_SIZE, page * PAGE_SIZE)

  const handleAnular = (id: string) => {
    setAnulandoId(id)
    setTimeout(() => setAnulandoId(null), 800)
  }

  return (
    <div className="flex flex-col gap-3">
      <h3 className="text-sm font-semibold uppercase text-gray-500">Tus últimas cargas</h3>

      {operacionesPagina.length === 0 ? (
        <p className="text-sm text-gray-500">Todavía no registraste ninguna operación.</p>
      ) : (
        operacionesPagina.map(({ id, ...operacion }) => (
          <OperacionCard key={id} {...operacion} anulando={anulandoId === id} onAnular={() => handleAnular(id)} />
        ))
      )}

      {totalPages > 1 && <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />}
    </div>
  )
}

export default ListadoCargas
