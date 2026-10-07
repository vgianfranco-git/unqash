import { Eye, FileText, Pencil, Trash2, User } from 'lucide-react'
import { formatCurrency } from '../../utils/currency'
import type { FacturaCardProps } from '../../types/components/FacturaCardProps'

function FacturaCard({ id, proveedor, detalles, fecha, usuario, monto, tieneFoto, onEditar, onVerFoto, onAnular }: FacturaCardProps) {
  return (
      <div className="flex items-center justify-between gap-4 rounded-2xl border border-green-light bg-white p-4">
        <div className="flex min-w-0 flex-1 items-center gap-4">
        <span className="flex h-9 w-9 shrink-0 items-center justify-center rounded-full bg-green-light text-green-main">
          <FileText size={18} />
        </span>
          <div className="min-w-0">
            <p className="flex min-w-0 items-baseline gap-1 font-semibold text-gray-800">
              <span className="min-w-16 shrink truncate" title={proveedor}>{proveedor}</span>
              {detalles && (
                <>
                  <span className="shrink-0 font-normal text-gray-400">·</span>
                  <span className="min-w-16 flex-1 truncate font-normal text-gray-600" title={detalles}>{detalles}</span>
                </>
              )}
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

        <div className="flex shrink-0 items-center gap-3">
          <span className="font-semibold text-gray-800">{formatCurrency(monto)}</span>

          <button
              type="button"
              aria-label="Ver foto de la factura"
              disabled={!tieneFoto}
              onClick={() => onVerFoto(id)}
              className="flex h-8 w-8 items-center justify-center rounded-full border border-green-light text-green-main transition-colors hover:bg-green-light disabled:cursor-not-allowed disabled:border-gray-200 disabled:text-gray-300 disabled:hover:bg-transparent"
          >
            <Eye size={16} />
          </button>

          <button
              type="button"
              aria-label="Editar factura"
              onClick={() => onEditar(id)}
              className="flex h-8 w-8 items-center justify-center rounded-full border border-green-light text-green-main transition-colors hover:bg-green-light"
          >
            <Pencil size={16} />
          </button>

          <button
              type="button"
              aria-label="Anular factura"
              onClick={() => onAnular(id)}
              className="flex h-8 w-8 items-center justify-center rounded-full border border-green-light text-green-main transition-colors hover:bg-green-light"
          >
            <Trash2 size={16} />
          </button>
        </div>
      </div>
  )
}

export default FacturaCard