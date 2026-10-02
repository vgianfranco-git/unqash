import { useState } from 'react'
import FacturaForm from '../components/features/FacturaForm'
import ListadoFacturas from '../components/features/ListadoFacturas'

function FacturasPage() {
  const [refreshKey, setRefreshKey] = useState(0)

  return (
    <div className="flex flex-col gap-6">
      <FacturaForm onFacturaRegistrada={() => setRefreshKey((key) => key + 1)} />
      <ListadoFacturas refreshKey={refreshKey} />
    </div>
  )
}

export default FacturasPage
