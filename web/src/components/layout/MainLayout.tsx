import { Outlet } from 'react-router-dom'
import { ToastContainer } from 'react-toastify'
import 'react-toastify/dist/ReactToastify.css'
import Header from './Header'

function MainLayout() {
  return (
    <div className="min-h-screen bg-background">
      <ToastContainer position="bottom-right" />
      <Header />
      <main className="mx-auto max-w-3xl px-4 py-8">
        <Outlet />
      </main>
    </div>
  )
}

export default MainLayout
