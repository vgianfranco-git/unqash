import { useEffect, useMemo, useState, type ReactNode } from 'react'
import { authService } from '../services/authService'
import type { UsuarioType } from '../types/services/UsuarioType'
import { AuthContext, type AuthContextValue } from './context'

export function AuthProvider({ children }: { children: ReactNode }) {
  const [usuario, setUsuario] = useState<UsuarioType | null>(null)
  const [cargandoSesion, setCargandoSesion] = useState(true)

  useEffect(() => {
    let activo = true

    authService.recuperarSesion()
      .then((usuarioAutenticado) => {
        if (activo) setUsuario(usuarioAutenticado)
      })
      .catch(() => {
        if (activo) setUsuario(null)
      })
      .finally(() => {
        if (activo) setCargandoSesion(false)
      })

    return () => {
      activo = false
    }
  }, [])

  const value = useMemo<AuthContextValue>(() => ({
    usuario,
    cargandoSesion,
    iniciarSesion: async (credenciales) => {
      const usuarioAutenticado = await authService.iniciarSesion(credenciales)
      setUsuario(usuarioAutenticado)
    },
    cerrarSesion: async () => {
      await authService.cerrarSesion()
      setUsuario(null)
    },
  }), [cargandoSesion, usuario])

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}
