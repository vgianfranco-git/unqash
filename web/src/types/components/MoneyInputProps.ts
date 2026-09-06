export interface MoneyInputProps {
  id?: string
  label: string
  value: number | undefined
  onValueChange: (value: number | undefined) => void
  disabled?: boolean
  error?: string
  max?: number
}
