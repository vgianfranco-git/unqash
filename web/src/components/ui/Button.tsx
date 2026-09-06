import type { ButtonProps } from '../../types/components/ButtonProps'

function Button({
  loading = false,
  loadingText = 'Cargando...',
  disabled,
  children,
  className,
  ...buttonProps
}: ButtonProps) {
  return (
    <button
      type="button"
      disabled={disabled || loading}
      className={`w-full rounded-xl bg-green-medium py-3 font-medium text-white transition-colors hover:bg-green-main disabled:cursor-not-allowed disabled:opacity-70 ${className ?? ''}`}
      {...buttonProps}
    >
      {loading ? loadingText : children}
    </button>
  )
}

export default Button
