import { Outlet } from 'react-router-dom'
import Header from './Header'

function MainLayout() {
  return (
    <div className="min-h-screen bg-background">
      <Header />
      <main className="mx-auto max-w-3xl px-4 py-8">
        <Outlet />
      </main>
    </div>
  )
}

export default MainLayout
