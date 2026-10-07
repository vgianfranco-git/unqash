import type { InputHTMLAttributes, ReactNode } from 'react'

export interface TextInputProps extends InputHTMLAttributes<HTMLInputElement> {
  label: string
  error?: string
  endAdornment?: ReactNode
}
