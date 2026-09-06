import logo from '../../assets/unqash_logo_hq_verde.png'
import NavItem from '../ui/NavItem'
import LogoutButton from '../ui/LogoutButton'
import type { NavTab } from '../../types/layout/NavTab'

// Tabs habilitadas. A medida que se implementen las pantallas de Gestión y
// Facturas, se descomentan sus entradas y aparecen automáticamente en el nav.
const NAV_TABS: NavTab[] = [
  { label: 'Ventas', path: '/' },
  { label: 'Gestión', path: '/gestion' },
  { label: 'Facturas', path: '/facturas' },
]

function Header() {
  return (
    <header className="flex items-center justify-between border-b border-green-light bg-white px-8 py-4">
      <div className="flex items-center gap-2">
        <img src={logo} alt="UNQash" className="h-8 w-8 object-contain" />
        <span className="text-lg font-semibold text-green-main">UNQash</span>
      </div>

      <nav className="flex items-center gap-1 rounded-full bg-green-light p-1">
        {NAV_TABS.map((tab) => (
          <NavItem key={tab.path} {...tab} />
        ))}
      </nav>

      <LogoutButton />
    </header>
  )
}

export default Header
