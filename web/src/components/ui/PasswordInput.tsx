import { useState } from 'react'
import { Eye, EyeOff } from 'lucide-react'
import TextInput from './TextInput'
import type { TextInputProps } from '../../types/components/TextInputProps'

type PasswordInputProps = Omit<TextInputProps, 'type' | 'endAdornment'>

function PasswordInput(props: PasswordInputProps) {
  const [mostrarContrasena, setMostrarContrasena] = useState(false)

  return (
    <TextInput
      {...props}
      type={mostrarContrasena ? 'text' : 'password'}
      endAdornment={
        <button
          type="button"
          onClick={() => setMostrarContrasena((prev) => !prev)}
          className="text-gray-400 hover:text-gray-600"
          aria-label={mostrarContrasena ? 'Ocultar contraseña' : 'Mostrar contraseña'}
          tabIndex={-1}
        >
          {mostrarContrasena ? <EyeOff size={18} /> : <Eye size={18} />}
        </button>
      }
    />
  )
}

export default PasswordInput
