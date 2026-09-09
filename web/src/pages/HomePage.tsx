import VentaForm from '../components/features/VentaForm'
import ListadoCargas from '../components/features/ListadoCargas'

function HomePage() {
  return (
    <div className="flex flex-col gap-6">
      <VentaForm />
      <ListadoCargas />
    </div>
  )
}

export default HomePage
