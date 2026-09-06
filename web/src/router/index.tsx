import { createBrowserRouter } from 'react-router-dom'
import MainLayout from '../components/layout/MainLayout'
import { HomePage, GestionPage, FacturasPage } from '../pages'

export const router = createBrowserRouter([
  {
    path: '/',
    element: <MainLayout />,
    children: [
      { index: true, element: <HomePage /> },
      { path: 'gestion', element: <GestionPage /> },
      { path: 'facturas', element: <FacturasPage /> },
    ],
  },
])
