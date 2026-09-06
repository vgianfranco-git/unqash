import { NavLink } from 'react-router-dom'
import type { NavTab } from '../../types/layout/NavTab'

function NavItem({ label, path }: NavTab) {
  return (
    <NavLink
      to={path}
      className={({ isActive }) =>
        `rounded-full px-4 py-1.5 text-sm font-medium transition-colors ${
          isActive
            ? 'bg-white text-green-main shadow-sm'
            : 'text-green-main/70 hover:text-green-main'
        }`
      }
    >
      {label}
    </NavLink>
  )
}

export default NavItem
