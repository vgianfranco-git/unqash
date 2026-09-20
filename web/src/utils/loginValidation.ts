import type { AuthFormErrors } from '../types/forms/AuthFormErrors'
import type { AuthRequestType } from '../types/services/AuthType'

export function validarLogin({ dni, contrasena }: AuthRequestType): AuthFormErrors {
  const errores: AuthFormErrors = {}

  if (!dni) errores.dni = 'Completá el DNI.'
  else if (!/^\d{7,8}$/.test(dni)) errores.dni = 'El DNI debe tener entre 7 y 8 dígitos.'

  if (!contrasena) errores.contrasena = 'Completá la contraseña.'
  else if (contrasena.length < 10 || contrasena.length > 30)
    errores.contrasena = 'La contraseña debe tener entre 10 y 30 caracteres.'

  return errores
}
