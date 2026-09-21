import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import logo from '../../assets/unqash_logo_hq_verde.png'
import { useAuth } from '../../auth/useAuth'
import NavItem from '../ui/NavItem'
import LogoutButton from '../ui/LogoutButton'
import type { NavTab } from '../../types/layout/NavTab'

// Tabs habilitadas. A medida que se implementen nuevas pantallas, se agregan
// sus entradas y aparecen automáticamente en el nav.
const NAV_TABS: NavTab[] = [
  { label: 'Ventas', path: '/' },
  { label: 'Usuarios', path: '/gestion' },
  // { label: 'Facturas', path: '/facturas' },
]

function Header() {
  const navigate = useNavigate()
  const { cerrarSesion } = useAuth()
  const [cerrandoSesion, setCerrandoSesion] = useState(false)

  const handleCerrarSesion = async () => {
    setCerrandoSesion(true)
    try {
      await cerrarSesion()
      navigate('/login', { replace: true })
    } finally {
      setCerrandoSesion(false)
    }
  }

  return (
    <header className="grid grid-cols-3 items-center border-b border-green-light bg-white px-8 py-4">
      <div className="flex items-center gap-2">
        <img src={logo} alt="UNQash" className="h-8 w-8 object-contain" />
        <span className="text-lg font-semibold text-green-main">UNQash</span>
      </div>

      <nav className="flex items-center justify-self-center gap-1 rounded-full bg-green-light p-1">
        {NAV_TABS.map((tab) => (
          <NavItem key={tab.path} {...tab} />
        ))}
      </nav>

      <div className="justify-self-end">
        <LogoutButton disabled={cerrandoSesion} onClick={handleCerrarSesion} />
      </div>
    </header>
  )
}

export default Header
