import { LogOut } from 'lucide-react'

function LogoutButton() {
  return (
    <button
      type="button"
      className="flex items-center gap-1.5 rounded-full px-3 py-1.5 text-sm font-medium text-green-main transition-colors hover:bg-green-light"
    >
      <LogOut size={16} />
      Salir
    </button>
  )
}

export default LogoutButton
