import { Trash2 } from 'lucide-react'
import Modal from './Modal'
import type { ConfirmModalProps } from '../../types/components/ConfirmModalProps'

function ConfirmModal({
  title,
  subtitle,
  confirmText = 'Sí',
  cancelText = 'No',
  isConfirming = false,
  onConfirm,
  onClose,
}: ConfirmModalProps) {
  return (
    <Modal widthClassName="max-w-md" closeOnBackdropClick={false} onClose={onClose}>
      <div className="flex flex-col items-center gap-3 pt-2 text-center">
        <span className="flex h-12 w-12 items-center justify-center rounded-full bg-red-100 text-red-500">
          <Trash2 size={22} />
        </span>

        <p className="text-lg font-bold text-gray-800">{title}</p>
        {subtitle && <p className="text-sm text-gray-500">{subtitle}</p>}

        <div className="mt-2 flex w-full gap-3">
          <button
            type="button"
            onClick={onClose}
            disabled={isConfirming}
            className="flex-1 rounded-xl border border-gray-200 py-2.5 font-medium text-gray-700 transition-colors hover:bg-gray-50 disabled:cursor-not-allowed disabled:opacity-60"
          >
            {cancelText}
          </button>
          <button
            type="button"
            onClick={onConfirm}
            disabled={isConfirming}
            className="flex-1 rounded-xl bg-red-500 py-2.5 font-medium text-white transition-colors hover:bg-red-500/90 disabled:cursor-not-allowed disabled:opacity-70"
          >
            {isConfirming ? 'Anulando...' : confirmText}
          </button>
        </div>
      </div>
    </Modal>
  )
}

export default ConfirmModal
