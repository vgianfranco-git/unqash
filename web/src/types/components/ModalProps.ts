import type { ReactNode } from 'react'

export interface ModalProps {
  title: string
  subtitle?: string
  widthClassName?: string
  onClose: () => void
  children: ReactNode
}
