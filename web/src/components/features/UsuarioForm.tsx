import { useState, type FormEvent } from 'react'
import Card from '../ui/Card'
import TextInput from '../ui/TextInput'
import Button from '../ui/Button'
import { usuarioService } from '../../services/usuarioService'
import { notificationService, handleApiError } from '../../services/notifications'
import { validarUsuario } from '../../utils/usuarioValidation'
import type { UsuarioFormErrors } from '../../types/forms/UsuarioFormErrors'
import type { UsuarioRequestType } from '../../types/services/UsuarioType'

const initialForm: UsuarioRequestType = {
  apellido: '',
  nombre: '',
  dni: '',
  email: '',
  telefono: '',
  contrasena: '',
}

function UsuarioForm() {
  const [form, setForm] = useState<UsuarioRequestType>(initialForm)
  const [errors, setErrors] = useState<UsuarioFormErrors>({})
  const [isSubmitting, setIsSubmitting] = useState(false)

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()

    const nuevosErrores = validarUsuario(form)
    setErrors(nuevosErrores)
    if (Object.keys(nuevosErrores).length > 0) return

    setIsSubmitting(true)
    try {
      await usuarioService.crear({
        ...form,
        apellido: form.apellido.trim(),
        nombre: form.nombre.trim(),
        email: form.email.trim(),
      })
      notificationService.success('Usuario creado con éxito')
      setForm(initialForm)
      setErrors({})
    } catch (error) {
      notificationService.error(handleApiError(error, 'No se pudo crear el usuario.'))
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <Card>
      <div className="mb-6">
        <h1 className="text-xl font-bold text-gray-800">Usuarios</h1>
        <p className="mt-1 text-sm text-gray-500">Completá los datos para crear una nueva cuenta.</p>
      </div>

      <form className="flex flex-col gap-4" noValidate onSubmit={handleSubmit}>
        <div className="grid gap-4 sm:grid-cols-2">
          <TextInput
            id="apellido"
            label="Apellido"
            placeholder="Ej. Pérez"
            autoComplete="family-name"
            maxLength={50}
            value={form.apellido}
            disabled={isSubmitting}
            error={errors.apellido}
            onChange={(event) => setForm({ ...form, apellido: event.target.value })}
          />
          <TextInput
            id="nombre"
            label="Nombre"
            placeholder="Ej. Ana"
            autoComplete="given-name"
            maxLength={50}
            value={form.nombre}
            disabled={isSubmitting}
            error={errors.nombre}
            onChange={(event) => setForm({ ...form, nombre: event.target.value })}
          />
        </div>

        <div className="grid gap-4 sm:grid-cols-2">
          <TextInput
            id="dni"
            label="DNI"
            placeholder="Ej. 40123456"
            inputMode="numeric"
            autoComplete="off"
            maxLength={8}
            value={form.dni}
            disabled={isSubmitting}
            error={errors.dni}
            onChange={(event) => setForm({ ...form, dni: event.target.value })}
          />
          <TextInput
            id="telefono"
            label="Número de teléfono"
            placeholder="Ej. 1122334455"
            inputMode="numeric"
            autoComplete="tel"
            maxLength={15}
            value={form.telefono}
            disabled={isSubmitting}
            error={errors.telefono}
            onChange={(event) => setForm({ ...form, telefono: event.target.value })}
          />
        </div>

        <TextInput
          id="email"
          label="Correo electrónico"
          placeholder="Ej. ana@comercio.test"
          type="email"
          autoComplete="email"
          maxLength={50}
          value={form.email}
          disabled={isSubmitting}
          error={errors.email}
          onChange={(event) => setForm({ ...form, email: event.target.value })}
        />

        <TextInput
          id="contrasena"
          label="Contraseña"
          placeholder="Mínimo 10 caracteres, una mayúscula y un símbolo"
          type="password"
          autoComplete="new-password"
          maxLength={30}
          value={form.contrasena}
          disabled={isSubmitting}
          error={errors.contrasena}
          onChange={(event) => setForm({ ...form, contrasena: event.target.value })}
        />

        <Button type="submit" loading={isSubmitting} loadingText="Creando usuario...">
          Crear usuario
        </Button>
      </form>
    </Card>
  )
}

export default UsuarioForm
