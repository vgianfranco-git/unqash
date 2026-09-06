import { Construction } from 'lucide-react'
import type { ComingSoonProps } from '../../types/components/ComingSoonProps'

function ComingSoon({ title }: ComingSoonProps) {
  return (
    <div className="flex flex-col items-center gap-3 rounded-2xl border border-green-light bg-white py-16 text-center">
      <Construction className="text-green-medium" size={32} />
      <h1 className="text-lg font-semibold text-gray-800">{title}</h1>
      <p className="text-sm text-gray-500">Esta sección está en preparación.</p>
    </div>
  )
}

export default ComingSoon
