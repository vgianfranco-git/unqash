import { useState } from 'react'
import VentaForm from '../components/features/VentaForm'
import ListadoCargas from '../components/features/ListadoCargas'

function HomePage() {
  const [refreshKey, setRefreshKey] = useState(0)

  return (
    <div className="flex flex-col gap-6">
      <VentaForm onVentaRegistrada={() => setRefreshKey((key) => key + 1)} />
      <ListadoCargas refreshKey={refreshKey} />
    </div>
  )
}

export default HomePage
