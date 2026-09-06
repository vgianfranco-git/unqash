import { NumericFormat } from 'react-number-format'
import type { MoneyInputProps } from '../../types/components/MoneyInputProps'

function MoneyInput({ id, label, value, onValueChange, disabled, error, max }: MoneyInputProps) {
  return (
    <div>
      <label htmlFor={id} className="mb-1 block text-xs font-semibold uppercase text-gray-500">
        {label}
      </label>
      <NumericFormat
        id={id}
        value={value ?? ''}
        onValueChange={(values) => onValueChange(values.floatValue)}
        isAllowed={(values) => {
          if (max === undefined) return true
          const [enteros] = values.value.split('.')
          return enteros.length <= String(max).length
        }}
        thousandSeparator="."
        decimalSeparator=","
        prefix="$ "
        allowNegative={false}
        placeholder="$ 0"
        disabled={disabled}
        className="w-full rounded-lg border-none bg-green-light px-4 py-2.5 text-gray-800 placeholder:text-gray-400 focus:outline-none focus:ring-2 focus:ring-green-main disabled:cursor-not-allowed disabled:opacity-60"
      />
      {error && <p className="mt-1 text-xs text-red-500">{error}</p>}
    </div>
  )
}

export default MoneyInput
