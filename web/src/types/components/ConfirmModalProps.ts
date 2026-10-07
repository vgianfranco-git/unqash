export interface ConfirmModalProps {
  title: string
  subtitle?: string
  confirmText?: string
  cancelText?: string
  isConfirming?: boolean
  onConfirm: () => void
  onClose: () => void
}
