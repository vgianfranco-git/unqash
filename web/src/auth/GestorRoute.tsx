import { Navigate, Outlet } from 'react-router-dom'
import { useAuth } from './useAuth'

function GestorRoute() {
  const { usuario } = useAuth()

  if (!usuario?.esGestor) return <Navigate to="/" replace />

  return <Outlet />
}

export default GestorRoute
