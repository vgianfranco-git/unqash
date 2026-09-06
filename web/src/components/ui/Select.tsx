import { Listbox } from '@headlessui/react'
import { Check, ChevronDown } from 'lucide-react'
import type { SelectProps } from '../../types/components/SelectProps'

function Select({ label, options, error, id, value, disabled, placeholder, onChange }: SelectProps) {
  const selected = options.find((option) => option.value === value)

  return (
    <div>
      <Listbox value={value} onChange={onChange} disabled={disabled}>
        <label htmlFor={id} className="mb-1 block text-xs font-semibold uppercase text-gray-500">
          {label}
        </label>
        <div className="relative">
          <Listbox.Button
            id={id}
            className="flex w-full items-center justify-between rounded-lg bg-green-light px-4 py-2.5 text-left text-gray-800 focus:outline-none focus:ring-2 focus:ring-green-main disabled:cursor-not-allowed disabled:opacity-60"
          >
            <span className={selected ? '' : 'text-gray-400'}>{selected?.label ?? placeholder}</span>
            <ChevronDown size={16} className="text-gray-500" />
          </Listbox.Button>

          <Listbox.Options className="absolute z-10 mt-1 w-full overflow-hidden rounded-lg bg-white py-1 shadow-lg ring-1 ring-green-light focus:outline-none">
            {options.map((option) => (
              <Listbox.Option
                key={option.value}
                value={option.value}
                className={({ active }) =>
                  `flex cursor-pointer items-center justify-between px-4 py-2 ${
                    active ? 'bg-green-medium/20 text-green-main' : 'text-gray-800'
                  }`
                }
              >
                {({ selected: isSelected }) => (
                  <>
                    {option.label}
                    {isSelected && <Check size={16} className="text-green-main" />}
                  </>
                )}
              </Listbox.Option>
            ))}
          </Listbox.Options>
        </div>
      </Listbox>
      {error && <p className="mt-1 text-xs text-red-500">{error}</p>}
    </div>
  )
}

export default Select
