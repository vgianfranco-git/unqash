import { useEffect, useState } from 'react'
import { Checkbox } from '@headlessui/react'
import Modal from '../ui/Modal'
import TextInput from '../ui/TextInput'
import PasswordInput from '../ui/PasswordInput'
import Button from '../ui/Button'
import { usuarioService } from '../../services/usuarioService'
import { notificationService, handleApiError } from '../../services/notifications'
import { validarEdicionUsuario } from '../../utils/usuarioValidation'
import type { UsuarioFormErrors } from '../../types/forms/UsuarioFormErrors'
import type { UsuarioEditRequestType } from '../../types/services/UsuarioType'
import type { EditarUsuarioModalProps } from '../../types/components/EditarUsuarioModalProps'

function EditarUsuarioModal({ usuarioId, onClose, onUsuarioActualizado }: EditarUsuarioModalProps) {
  const [cargando, setCargando] = useState(true)
  const [errorCarga, setErrorCarga] = useState<string | null>(null)
  const [form, setForm] = useState<UsuarioEditRequestType | null>(null)
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
          esGestor: usuario.esGestor,
        })
      })
      .catch((err) => setErrorCarga(handleApiError(err, 'No se pudo cargar el usuario.')))
      .finally(() => setCargando(false))
  }, [usuarioId])

  const handleSubmit = async () => {
    if (!form) return

    const nuevosErrores = validarEdicionUsuario(form)
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
        esGestor: form.esGestor,
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
              maxLength={50}
              value={form.apellido}
              disabled={isSubmitting}
              error={errors.apellido}
              onChange={(e) => setForm({ ...form, apellido: e.target.value })}
            />
            <TextInput
              id="editar-nombre"
              label="Nombre"
              maxLength={50}
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
            autoComplete="new-password"
            maxLength={30}
            value={form.contrasena}
            disabled={isSubmitting}
            error={errors.contrasena}
            onChange={(e) => setForm({ ...form, contrasena: e.target.value })}
          />

          <label className="flex cursor-pointer items-center gap-2">
            <Checkbox
              checked={form.esGestor}
              disabled={isSubmitting}
              onChange={(checked) => setForm({ ...form, esGestor: checked })}
              className="flex h-5 w-5 items-center justify-center rounded border-none bg-green-light focus:outline-none focus:ring-2 focus:ring-green-main disabled:cursor-not-allowed disabled:opacity-60 data-[checked]:bg-green-main"
            >
              {form.esGestor && (
                <svg className="h-3 w-3 text-white" viewBox="0 0 12 12" fill="none">
                  <path d="M2 6l3 3 5-5" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
                </svg>
              )}
            </Checkbox>
            <span className="text-sm text-gray-700">Gestor</span>
          </label>

          <Button loading={isSubmitting} loadingText="Guardando..." onClick={handleSubmit}>
            Guardar cambios
          </Button>
        </div>
      )}
    </Modal>
  )
}

export default EditarUsuarioModal
