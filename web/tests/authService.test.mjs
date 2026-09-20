import assert from 'node:assert/strict'
import test from 'node:test'
import { createAuthService } from '../src/services/authService.ts'

const usuario = {
  id: '00000000-0000-0000-0000-000000000010',
  apellido: 'Prueba',
  nombre: 'Usuario',
  dni: '40123456',
  email: 'usuario.auth@example.test',
  telefono: '1112345678',
  fechaHoraAlta: '2026-09-19T10:00:00',
  usuarioDeAlta: 'SUPER_ADMIN',
}

test('envía las credenciales al endpoint de login y devuelve el usuario autenticado', async () => {
  const solicitudes = []
  const authService = createAuthService({
    post: async (ruta, cuerpo) => {
      solicitudes.push({ metodo: 'POST', ruta, cuerpo })
      return { data: usuario }
    },
    get: async () => ({ data: usuario }),
  })

  const resultado = await authService.iniciarSesion({ dni: '40123456', contrasena: 'contra1234' })

  assert.deepEqual(resultado, usuario)
  assert.deepEqual(solicitudes, [{
    metodo: 'POST',
    ruta: '/auth/login',
    cuerpo: { dni: '40123456', contrasena: 'contra1234' },
  }])
})

test('recupera la sesión activa desde el endpoint correspondiente', async () => {
  const solicitudes = []
  const authService = createAuthService({
    post: async () => ({ data: usuario }),
    get: async (ruta) => {
      solicitudes.push({ metodo: 'GET', ruta })
      return { data: usuario }
    },
  })

  const resultado = await authService.recuperarSesion()

  assert.deepEqual(resultado, usuario)
  assert.deepEqual(solicitudes, [{ metodo: 'GET', ruta: '/auth/sesion' }])
})

test('invalida la sesión activa mediante el endpoint de logout', async () => {
  const solicitudes = []
  const authService = createAuthService({
    post: async () => ({ data: usuario }),
    get: async () => ({ data: usuario }),
    delete: async (ruta) => {
      solicitudes.push({ metodo: 'DELETE', ruta })
    },
  })

  await authService.cerrarSesion()

  assert.deepEqual(solicitudes, [{ metodo: 'DELETE', ruta: '/auth/sesion' }])
})
