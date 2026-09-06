import type { ReactNode } from 'react'

function Card({ children }: { children: ReactNode }) {
  return <div className="rounded-2xl border border-green-light bg-white p-6">{children}</div>
}

export default Card
