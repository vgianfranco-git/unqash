import { Minus, Plus } from 'lucide-react'
import { formatCurrency } from '../../utils/currency'
import type { OperacionCardProps } from '../../types/components/OperacionCardProps'

function OperacionCard({ producto, fecha, tipoCobro, monto, tipo, anulando, onAnular }: OperacionCardProps) {
  const esAnulacion = tipo === 'anulacion'

  return (
    <div className="flex items-center justify-between rounded-2xl border border-green-light bg-white p-4">
      <div className="flex items-center gap-4">
        <span
          className={`flex h-9 w-9 shrink-0 items-center justify-center rounded-full ${
            esAnulacion ? 'bg-red-100 text-red-500' : 'bg-green-light text-green-main'
          }`}
        >
          {esAnulacion ? <Minus size={18} /> : <Plus size={18} />}
        </span>
        <div>
          <p className="font-semibold text-gray-800">{producto}</p>
          <p className="text-sm text-gray-500">
            {fecha} • {tipoCobro}
          </p>
        </div>
      </div>

      <div className="flex items-center gap-3">
        <span className={`font-semibold ${esAnulacion ? 'text-red-500' : 'text-green-700'}`}>
          {esAnulacion ? '-' : ''}
          {formatCurrency(monto)}
        </span>
        {!esAnulacion && (
          <button
            type="button"
            disabled={anulando}
            onClick={onAnular}
            className="rounded-full border border-gray-200 px-3 py-1 text-sm text-gray-600 transition-colors hover:bg-gray-50 disabled:cursor-not-allowed disabled:opacity-60"
          >
            {anulando ? 'Anulando...' : 'Anular'}
          </button>
        )}
      </div>
    </div>
  )
}

export default OperacionCard
