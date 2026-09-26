import { createBrowserRouter } from 'react-router-dom'
import ProtectedRoute from '../auth/ProtectedRoute'
import GestorRoute from '../auth/GestorRoute'
import MainLayout from '../components/layout/MainLayout'
import { HomePage, GestionPage, UsuariosPage, FacturasPage, LoginPage } from '../pages'

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
          { path: 'facturas', element: <FacturasPage /> },
          {
            element: <GestorRoute />,
            children: [
              { path: 'gestion', element: <GestionPage /> },
              { path: 'usuarios', element: <UsuariosPage /> },
            ],
          },
        ],
      },
    ],
  },
])
