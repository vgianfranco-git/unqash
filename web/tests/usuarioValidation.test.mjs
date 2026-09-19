import assert from 'node:assert/strict'
import test from 'node:test'
import { validarUsuario } from '../src/utils/usuarioValidation.ts'

const usuarioValido = {
  apellido: 'Pérez',
  nombre: 'Ana María',
  dni: '40123456',
  email: 'ana.perez@example.test',
  telefono: '1122334455',
  contrasena: 'ClaveSegura!',
}

test('acepta un usuario cuyos datos cumplen todos los criterios de alta', () => {
  assert.deepEqual(validarUsuario(usuarioValido), {})
})

test('informa los campos obligatorios cuando se intenta enviar un formulario vacío', () => {
  assert.deepEqual(validarUsuario({
    apellido: '',
    nombre: '',
    dni: '',
    email: '',
    telefono: '',
    contrasena: '',
  }), {
    apellido: 'Completá el apellido.',
    nombre: 'Completá el nombre.',
    dni: 'Completá el DNI.',
    email: 'Completá el correo electrónico.',
    telefono: 'Completá el teléfono.',
    contrasena: 'Completá la contraseña.',
  })
})

test('rechaza formatos inválidos para los campos de usuario', () => {
  const errores = validarUsuario({
    apellido: 'Pérez1',
    nombre: 'Ana2',
    dni: '123456',
    email: 'correo-invalido',
    telefono: '11-2233',
    contrasena: 'sinmayuscula!',
  })

  assert.deepEqual(errores, {
    apellido: 'El apellido solo puede contener letras.',
    nombre: 'El nombre solo puede contener letras.',
    dni: 'El DNI debe tener entre 7 y 8 dígitos.',
    email: 'Ingresá un correo electrónico válido.',
    telefono: 'El teléfono debe tener entre 8 y 15 dígitos.',
    contrasena: 'La contraseña debe tener entre 10 y 30 caracteres, una mayúscula y un símbolo.',
  })
})

test('rechaza longitudes mayores a las permitidas', () => {
  const errores = validarUsuario({
    ...usuarioValido,
    apellido: 'A'.repeat(51),
    nombre: 'B'.repeat(51),
    email: `${'c'.repeat(40)}@correo.test`,
    telefono: '1'.repeat(16),
    contrasena: 'A!123456789012345678901234567890',
  })

  assert.deepEqual(errores, {
    apellido: 'El apellido debe tener como máximo 50 caracteres.',
    nombre: 'El nombre debe tener como máximo 50 caracteres.',
    email: 'El correo electrónico debe tener como máximo 50 caracteres.',
    telefono: 'El teléfono debe tener entre 8 y 15 dígitos.',
    contrasena: 'La contraseña debe tener entre 10 y 30 caracteres, una mayúscula y un símbolo.',
  })
})
