import { useEffect, useState } from 'react'
import UsuarioCard from './UsuarioCard'
import Pagination from '../ui/Pagination'
import { usuarioService } from '../../services/usuarioService'
import { notificationService, handleApiError } from '../../services/notifications'
import type { UsuarioHistorialType } from '../../types/services/UsuarioType'
import type { ListadoUsuariosProps } from '../../types/components/ListadoUsuariosProps'

function ListadoUsuarios({ refreshKey }: ListadoUsuariosProps) {
  const [page, setPage] = useState(1)
  const [usuarios, setUsuarios] = useState<UsuarioHistorialType[]>([])
  const [totalPages, setTotalPages] = useState(1)
  const [cargando, setCargando] = useState(true)

  useEffect(() => {
    setCargando(true)
    usuarioService
      .listar(page)
      .then((respuesta) => {
        setUsuarios(respuesta.usuarios)
        setTotalPages(respuesta.totalPag)
      })
      .catch((error) => notificationService.error(handleApiError(error, 'No se pudo cargar el listado de usuarios.')))
      .finally(() => setCargando(false))
  }, [page, refreshKey])

  return (
    <div className="flex flex-col gap-3">
      <h3 className="text-sm font-semibold uppercase text-gray-500">Usuarios</h3>

      {cargando ? (
        <p className="text-sm text-gray-500">Cargando...</p>
      ) : usuarios.length === 0 ? (
        <p className="text-center text-sm text-gray-500">Todavía no hay usuarios registrados.</p>
      ) : (
        usuarios.map((usuario) => (
          <UsuarioCard
            key={usuario.id}
            apellido={usuario.apellido}
            nombre={usuario.nombre}
            dni={usuario.dni}
            telefono={usuario.telefono}
            email={usuario.email}
          />
        ))
      )}

      {totalPages > 1 && <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />}
    </div>
  )
}

export default ListadoUsuarios
