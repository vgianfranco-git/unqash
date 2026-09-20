import { Navigate } from 'react-router-dom'
import { useAuth } from '../auth/useAuth'
import LoginForm from '../components/features/LoginForm'

function LoginPage() {
  const { usuario, cargandoSesion } = useAuth()

  if (cargandoSesion) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-background text-sm text-gray-500" role="status">
        Cargando sesión...
      </div>
    )
  }

  if (usuario) return <Navigate to="/" replace />

  return (
    <main className="flex min-h-screen items-center justify-center bg-background px-4 py-8">
      <div className="w-full max-w-md">
        <LoginForm />
      </div>
    </main>
  )
}

export default LoginPage
