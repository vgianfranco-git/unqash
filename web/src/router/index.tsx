import { createBrowserRouter } from 'react-router-dom'
import ProtectedRoute from '../auth/ProtectedRoute'
import MainLayout from '../components/layout/MainLayout'
import { HomePage, GestionPage, FacturasPage, LoginPage } from '../pages'

export const router = createBrowserRouter([
  {
    path: '/login',
    element: <LoginPage />,
  },
  {
    path: '/',
    element: <ProtectedRoute />,
    children: [
      {
        element: <MainLayout />,
        children: [
          { index: true, element: <HomePage /> },
          { path: 'gestion', element: <GestionPage /> },
          { path: 'facturas', element: <FacturasPage /> },
        ],
      },
    ],
  },
])
