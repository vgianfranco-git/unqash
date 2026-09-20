import assert from 'node:assert/strict'
import test from 'node:test'
import { validarLogin } from '../src/utils/loginValidation.ts'

test('acepta credenciales con DNI de 7 u 8 dígitos y contraseña dentro del rango permitido', () => {
  assert.deepEqual(validarLogin({ dni: '40123456', contrasena: 'contra1234' }), {})
  assert.deepEqual(validarLogin({ dni: '1234567', contrasena: 'a'.repeat(30) }), {})
})

test('informa los campos obligatorios al enviar el login vacío', () => {
  assert.deepEqual(validarLogin({ dni: '', contrasena: '' }), {
    dni: 'Completá el DNI.',
    contrasena: 'Completá la contraseña.',
  })
})

test('rechaza DNI no numérico y contraseña fuera del rango de login', () => {
  assert.deepEqual(validarLogin({ dni: '40A123', contrasena: 'corta' }), {
    dni: 'El DNI debe tener entre 7 y 8 dígitos.',
    contrasena: 'La contraseña debe tener entre 10 y 30 caracteres.',
  })
})
