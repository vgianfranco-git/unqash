import { useState } from 'react'
import UsuarioForm from '../components/features/UsuarioForm'
import ListadoUsuarios from '../components/features/ListadoUsuarios'

function UsuariosPage() {
  const [refreshKey, setRefreshKey] = useState(0)

  return (
    <div className="flex flex-col gap-3">
      <UsuarioForm onUsuarioRegistrado={() => setRefreshKey((key) => key + 1)} />
      <ListadoUsuarios refreshKey={refreshKey} />
    </div>
  )
}

export default UsuariosPage
