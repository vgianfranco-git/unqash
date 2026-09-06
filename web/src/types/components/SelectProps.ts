export interface SelectOption {
  value: string
  label: string
}

export interface SelectProps {
  id?: string
  label: string
  options: SelectOption[]
  value: string
  onChange: (value: string) => void
  disabled?: boolean
  error?: string
  placeholder?: string
}
