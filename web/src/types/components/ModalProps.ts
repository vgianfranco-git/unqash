import type { ReactNode } from 'react'

export interface ModalProps {
  title?: string
  subtitle?: string
  widthClassName?: string
  closeOnBackdropClick?: boolean
  onClose: () => void
  children: ReactNode
}
