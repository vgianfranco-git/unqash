import type { UsuarioFormErrors } from '../types/forms/UsuarioFormErrors'
import type { UsuarioRequestType } from '../types/services/UsuarioType'

const SOLO_LETRAS = /^[\p{L} ]+$/u
const EMAIL_VALIDO = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
const CONTRASENA_VALIDA = /^(?=.{10,30}$)(?=.*[A-Z])(?=.*[^A-Za-z0-9]).*$/

export function validarUsuario(usuario: UsuarioRequestType): UsuarioFormErrors {
  const errores: UsuarioFormErrors = {}
  const apellido = usuario.apellido.trim()
  const nombre = usuario.nombre.trim()
  const email = usuario.email.trim()

  if (!apellido) errores.apellido = 'Completá el apellido.'
  else if (!SOLO_LETRAS.test(apellido)) errores.apellido = 'El apellido solo puede contener letras.'
  else if (apellido.length > 50) errores.apellido = 'El apellido debe tener como máximo 50 caracteres.'

  if (!nombre) errores.nombre = 'Completá el nombre.'
  else if (!SOLO_LETRAS.test(nombre)) errores.nombre = 'El nombre solo puede contener letras.'
  else if (nombre.length > 50) errores.nombre = 'El nombre debe tener como máximo 50 caracteres.'

  if (!usuario.dni) errores.dni = 'Completá el DNI.'
  else if (!/^\d{7,8}$/.test(usuario.dni)) errores.dni = 'El DNI debe tener entre 7 y 8 dígitos.'

  if (!email) errores.email = 'Completá el correo electrónico.'
  else if (email.length > 50) errores.email = 'El correo electrónico debe tener como máximo 50 caracteres.'
  else if (!EMAIL_VALIDO.test(email)) errores.email = 'Ingresá un correo electrónico válido.'

  if (!usuario.telefono) errores.telefono = 'Completá el teléfono.'
  else if (!/^\d{8,15}$/.test(usuario.telefono)) errores.telefono = 'El teléfono debe tener entre 8 y 15 dígitos.'

  if (!usuario.contrasena) errores.contrasena = 'Completá la contraseña.'
  else if (!CONTRASENA_VALIDA.test(usuario.contrasena))
    errores.contrasena = 'La contraseña debe tener entre 10 y 30 caracteres, una mayúscula y un símbolo.'

  return errores
}
