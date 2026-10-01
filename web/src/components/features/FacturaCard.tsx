import { FileText, User } from 'lucide-react'
import { formatCurrency } from '../../utils/currency'
import type { FacturaCardProps } from '../../types/components/FacturaCardProps'

function FacturaCard({ proveedor, detalles, fecha, usuario, monto }: FacturaCardProps) {
  return (
    <div className="flex items-center justify-between rounded-2xl border border-green-light bg-white p-4">
      <div className="flex items-center gap-4">
        <span className="flex h-9 w-9 shrink-0 items-center justify-center rounded-full bg-green-light text-green-main">
          <FileText size={18} />
        </span>
        <div>
          <p className="font-semibold text-gray-800">
            {proveedor}
            {detalles && ` · ${detalles}`}
          </p>
          <div className="flex flex-wrap items-center gap-2 text-sm text-gray-500">
            <span>{fecha}</span>
            <span className="flex items-center gap-1 rounded-full bg-green-light px-2 py-0.5 text-xs text-green-main">
              <User size={12} />
              {usuario}
            </span>
          </div>
        </div>
      </div>

      <span className="font-semibold text-gray-800">{formatCurrency(monto)}</span>
    </div>
  )
}

export default FacturaCard
