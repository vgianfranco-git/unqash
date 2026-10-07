import { api } from './api'
import type { UsuarioHistorialPageType, UsuarioHistorialType, UsuarioRequestType, UsuarioType } from '../types/services/UsuarioType'

const TAMANIO_PAGINA = 3

// Mock temporal: todavía no existe el endpoint GET /usuarios paginado en el backend.
// Ordenado del último usuario creado al primero, como pide el listado.
const USUARIOS_MOCK: UsuarioHistorialType[] = [
  { id: '10', apellido: 'Gómez', nombre: 'Juan', dni: '36789023', telefono: '1144556677', email: 'juan.gomez@comercio.test' },
  { id: '9', apellido: 'López', nombre: 'Martina', dni: '38456712', telefono: '1155667788', email: 'martina.lopez@comercio.test' },
  { id: '8', apellido: 'Pérez', nombre: 'Ana', dni: '40123456', telefono: '1122334455', email: 'ana.perez@comercio.test' },
  { id: '7', apellido: 'Fernández', nombre: 'Lucas', dni: '39876543', telefono: '1166778899', email: 'lucas.fernandez@comercio.test' },
  { id: '6', apellido: 'Rodríguez', nombre: 'Sofía', dni: '37654321', telefono: '1199887766', email: 'sofia.rodriguez@comercio.test' },
  { id: '5', apellido: 'Martínez', nombre: 'Tomás', dni: '35987654', telefono: '1133445566', email: 'tomas.martinez@comercio.test' },
  { id: '4', apellido: 'García', nombre: 'Valentina', dni: '41234567', telefono: '1177889900', email: 'valentina.garcia@comercio.test' },
  { id: '3', apellido: 'Sánchez', nombre: 'Mateo', dni: '34567890', telefono: '1188990011', email: 'mateo.sanchez@comercio.test' },
  { id: '2', apellido: 'Díaz', nombre: 'Camila', dni: '38123456', telefono: '1100112233', email: 'camila.diaz@comercio.test' },
  { id: '1', apellido: 'Romero', nombre: 'Nicolás', dni: '33456789', telefono: '1122114455', email: 'nicolas.romero@comercio.test' },
]

export const usuarioService = {
  crear: async (usuario: UsuarioRequestType): Promise<UsuarioType> => {
    const { data } = await api.post<UsuarioType>('/usuarios', usuario)
    return data
  },
  listar: async (page: number): Promise<UsuarioHistorialPageType> => {
    const totalPag = Math.max(1, Math.ceil(USUARIOS_MOCK.length / TAMANIO_PAGINA))
    const inicio = (page - 1) * TAMANIO_PAGINA
    return {
      usuarios: USUARIOS_MOCK.slice(inicio, inicio + TAMANIO_PAGINA),
      numPag: page,
      totalPag,
    }
  },
}
