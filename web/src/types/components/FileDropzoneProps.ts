export interface FileDropzoneProps {
  id: string
  label: string
  accept: string
  hint: string
  file: File | null
  error?: string
  disabled?: boolean
  onFileChange: (file: File | null) => void
}
