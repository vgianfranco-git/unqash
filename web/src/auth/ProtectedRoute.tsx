import { Navigate, Outlet } from 'react-router-dom'
import { useAuth } from './useAuth'

function ProtectedRoute() {
  const { usuario, cargandoSesion } = useAuth()

  if (cargandoSesion) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-background text-sm text-gray-500" role="status">
        Cargando sesión...
      </div>
    )
  }

  if (!usuario) return <Navigate to="/login" replace />

  return <Outlet />
}

export default ProtectedRoute
