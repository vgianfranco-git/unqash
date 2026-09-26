import { useState, type FormEvent } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../../auth/useAuth'
import Button from '../ui/Button'
import Card from '../ui/Card'
import TextInput from '../ui/TextInput'
import { validarLogin } from '../../utils/loginValidation'
import type { AuthFormErrors } from '../../types/forms/AuthFormErrors'
import type { AuthRequestType } from '../../types/services/AuthType'
import logo from '../../assets/unqash_logo_hq_verde.png'

const initialForm: AuthRequestType = {
  dni: '',
  contrasena: '',
}

function LoginForm() {
  const navigate = useNavigate()
  const { iniciarSesion } = useAuth()
  const [form, setForm] = useState<AuthRequestType>(initialForm)
  const [errors, setErrors] = useState<AuthFormErrors>({})
  const [errorGenerico, setErrorGenerico] = useState('')
  const [isSubmitting, setIsSubmitting] = useState(false)

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()

    const nuevosErrores = validarLogin(form)
    setErrors(nuevosErrores)
    setErrorGenerico('')
    if (Object.keys(nuevosErrores).length > 0) {
      setErrorGenerico('Ingresá un DNI y una contraseña válidos.')
      return
    }

    setIsSubmitting(true)
    try {
      await iniciarSesion(form)
      navigate('/', { replace: true })
    } catch {
      setErrorGenerico('DNI o contraseña incorrectos.')
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <Card>
      <div className="mb-6 flex flex-col items-center text-center">
        <img src={logo} alt="Logo de UNQash" className="h-20 w-20 object-contain" />
        <h1 className="mt-2 text-2xl font-bold text-gray-800">UNQash</h1>
      </div>

      <form className="flex flex-col gap-4" noValidate onSubmit={handleSubmit}>
        <TextInput
          id="dni"
          label="DNI"
          placeholder="Ej. 12345678"
          inputMode="numeric"
          autoComplete="username"
          maxLength={8}
          value={form.dni}
          disabled={isSubmitting}
          error={errors.dni}
          onChange={(event) => setForm({ ...form, dni: event.target.value.replace(/\D/g, '') })}
        />

        <TextInput
          id="contrasena"
          label="Contraseña"
          placeholder="Escribe tu contraseña"
          type="password"
          autoComplete="current-password"
          maxLength={30}
          value={form.contrasena}
          disabled={isSubmitting}
          error={errors.contrasena}
          onChange={(event) => setForm({ ...form, contrasena: event.target.value })}
        />

        {errorGenerico && <p className="text-center text-sm text-red-500" role="alert">{errorGenerico}</p>}

        <Button type="submit" loading={isSubmitting} loadingText="Ingresando...">
          Ingresar
        </Button>
      </form>
    </Card>
  )
}

export default LoginForm
