import { useEffect } from 'react'
import { X } from 'lucide-react'
import type { ModalProps } from '../../types/components/ModalProps'

function Modal({ title, subtitle, widthClassName, onClose, children }: ModalProps) {
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape') onClose()
    }
    document.addEventListener('keydown', handleKeyDown)
    return () => document.removeEventListener('keydown', handleKeyDown)
  }, [onClose])

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4" onClick={onClose}>
      <div
        className={`relative max-h-[90vh] w-full overflow-y-auto rounded-2xl bg-white p-6 shadow-xl ${widthClassName ?? 'max-w-lg'}`}
        onClick={(e) => e.stopPropagation()}
      >
        <button
          type="button"
          aria-label="Cerrar"
          onClick={onClose}
          className="absolute right-4 top-4 flex h-8 w-8 items-center justify-center rounded-full bg-green-light text-gray-600 transition-colors hover:bg-green-main/20"
        >
          <X size={16} />
        </button>

        <h2 className="mb-1 pr-8 text-lg font-bold text-gray-800">{title}</h2>
        {subtitle && <p className="mb-4 text-sm text-gray-500">{subtitle}</p>}

        {children}
      </div>
    </div>
  )
}

export default Modal
