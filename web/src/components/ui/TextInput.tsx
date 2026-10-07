import type { TextInputProps } from '../../types/components/TextInputProps'

function TextInput({ label, error, id, className, endAdornment, ...inputProps }: TextInputProps) {
  return (
    <div>
      <label htmlFor={id} className="mb-1 block text-xs font-semibold uppercase text-gray-500">
        {label}
      </label>
      <div className="relative">
        <input
          id={id}
          className={`w-full rounded-lg border-none bg-green-light px-4 py-2.5 text-gray-800 placeholder:text-gray-400 focus:outline-none focus:ring-2 focus:ring-green-main disabled:cursor-not-allowed disabled:opacity-60 ${endAdornment ? 'pr-10' : ''} ${className ?? ''}`}
          {...inputProps}
        />
        {endAdornment && (
          <div className="absolute inset-y-0 right-0 flex items-center pr-3">{endAdornment}</div>
        )}
      </div>
      {error && <p className="mt-1 text-xs text-red-500">{error}</p>}
    </div>
  )
}

export default TextInput
