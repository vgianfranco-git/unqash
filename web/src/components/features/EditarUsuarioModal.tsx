import { useEffect, useState } from 'react'
import Modal from '../ui/Modal'
import TextInput from '../ui/TextInput'
import PasswordInput from '../ui/PasswordInput'
import Button from '../ui/Button'
import { usuarioService } from '../../services/usuarioService'
import { notificationService, handleApiError } from '../../services/notifications'
import { validarUsuario } from '../../utils/usuarioValidation'
import type { UsuarioFormErrors } from '../../types/forms/UsuarioFormErrors'
import type { UsuarioRequestType } from '../../types/services/UsuarioType'
import type { EditarUsuarioModalProps } from '../../types/components/EditarUsuarioModalProps'

function EditarUsuarioModal({ usuarioId, onClose, onUsuarioActualizado }: EditarUsuarioModalProps) {
  const [cargando, setCargando] = useState(true)
  const [errorCarga, setErrorCarga] = useState<string | null>(null)
  const [form, setForm] = useState<UsuarioRequestType | null>(null)
  const [errors, setErrors] = useState<UsuarioFormErrors>({})
  const [isSubmitting, setIsSubmitting] = useState(false)

  useEffect(() => {
    usuarioService
      .obtener(usuarioId)
      .then((usuario) => {
        setForm({
          apellido: usuario.apellido,
          nombre: usuario.nombre,
          dni: usuario.dni,
          telefono: usuario.telefono,
          email: usuario.email,
          contrasena: '',
          esGestor: false,
        })
      })
      .catch((err) => setErrorCarga(handleApiError(err, 'No se pudo cargar el usuario.')))
      .finally(() => setCargando(false))
  }, [usuarioId])

  const handleSubmit = async () => {
    if (!form) return

    const nuevosErrores = validarUsuario(form)
    setErrors(nuevosErrores)
    if (Object.keys(nuevosErrores).length > 0) return

    setIsSubmitting(true)
    try {
      await usuarioService.editar(usuarioId, {
        apellido: form.apellido.trim(),
        nombre: form.nombre.trim(),
        dni: form.dni,
        telefono: form.telefono,
        email: form.email.trim(),
        contrasena: form.contrasena,
      })
      notificationService.success('Usuario actualizado con éxito')
      onUsuarioActualizado()
      onClose()
    } catch (error) {
      notificationService.error(handleApiError(error, 'No se pudo actualizar el usuario.'))
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <Modal title="Editar usuario" widthClassName="max-w-xl" closeOnBackdropClick={false} onClose={onClose}>
      {cargando && <p className="py-10 text-center text-sm text-gray-500">Cargando...</p>}

      {!cargando && errorCarga && <p className="py-10 text-center text-sm text-red-500">{errorCarga}</p>}

      {!cargando && !errorCarga && form && (
        <div className="flex flex-col gap-4">
          <div className="grid gap-4 sm:grid-cols-2">
            <TextInput
              id="editar-apellido"
              label="Apellido"
              value={form.apellido}
              disabled={isSubmitting}
              error={errors.apellido}
              onChange={(e) => setForm({ ...form, apellido: e.target.value })}
            />
            <TextInput
              id="editar-nombre"
              label="Nombre"
              value={form.nombre}
              disabled={isSubmitting}
              error={errors.nombre}
              onChange={(e) => setForm({ ...form, nombre: e.target.value })}
            />
          </div>

          <div className="grid gap-4 sm:grid-cols-2">
            <TextInput
              id="editar-dni"
              label="DNI"
              inputMode="numeric"
              maxLength={8}
              value={form.dni}
              disabled={isSubmitting}
              error={errors.dni}
              onChange={(e) => setForm({ ...form, dni: e.target.value })}
            />
            <TextInput
              id="editar-telefono"
              label="Número de teléfono"
              inputMode="numeric"
              maxLength={15}
              value={form.telefono}
              disabled={isSubmitting}
              error={errors.telefono}
              onChange={(e) => setForm({ ...form, telefono: e.target.value })}
            />
          </div>

          <TextInput
            id="editar-email"
            label="Correo electrónico"
            type="email"
            maxLength={50}
            value={form.email}
            disabled={isSubmitting}
            error={errors.email}
            onChange={(e) => setForm({ ...form, email: e.target.value })}
          />

          <PasswordInput
            id="editar-contrasena"
            label="Contraseña"
            placeholder="Mínimo 10 caracteres, una mayúscula y un símbolo"
            maxLength={30}
            value={form.contrasena}
            disabled={isSubmitting}
            error={errors.contrasena}
            onChange={(e) => setForm({ ...form, contrasena: e.target.value })}
          />

          <Button loading={isSubmitting} loadingText="Guardando..." onClick={handleSubmit}>
            Guardar cambios
          </Button>
        </div>
      )}
    </Modal>
  )
}

export default EditarUsuarioModal
