import { Pencil } from 'lucide-react'
import { obtenerIniciales } from '../../utils/iniciales'
import type { UsuarioCardProps } from '../../types/components/UsuarioCardProps'

function UsuarioCard({ id, apellido, nombre, dni, telefono, email, onEditar }: UsuarioCardProps) {
  return (
    <div className="flex items-center justify-between rounded-2xl border border-green-light bg-white p-4">
      <div className="flex items-center gap-4">
        <span className="flex h-9 w-9 shrink-0 items-center justify-center rounded-full bg-green-light text-xs font-semibold text-green-main">
          {obtenerIniciales(nombre, apellido)}
        </span>
        <div>
          <p className="font-semibold text-gray-800">{apellido}, {nombre} · DNI {dni}</p>
          <p className="text-sm text-gray-500">{email} · Tel. {telefono}</p>
        </div>
      </div>

      <button
        type="button"
        aria-label="Editar usuario"
        onClick={() => onEditar(id)}
        className="flex h-8 w-8 shrink-0 items-center justify-center rounded-full border border-green-light text-green-main transition-colors hover:bg-green-light"
      >
        <Pencil size={16} />
      </button>
    </div>
  )
}

export default UsuarioCard
