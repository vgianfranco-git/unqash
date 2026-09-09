import { ChevronLeft, ChevronRight } from 'lucide-react'
import type { PaginationProps } from '../../types/components/PaginationProps'

function getPageNumbers(page: number, totalPages: number): (number | 'ellipsis')[] {
  const delta = 1
  const left = Math.max(2, page - delta)
  const right = Math.min(totalPages - 1, page + delta)
  const pages: (number | 'ellipsis')[] = [1]

  if (left > 2) pages.push('ellipsis')
  for (let i = left; i <= right; i++) pages.push(i)
  if (right < totalPages - 1) pages.push('ellipsis')
  if (totalPages > 1) pages.push(totalPages)

  return pages
}

function Pagination({ page, totalPages, onPageChange }: PaginationProps) {
  const pages = getPageNumbers(page, totalPages)

  return (
    <div className="flex items-center justify-center gap-2">
      <button
        type="button"
        disabled={page === 1}
        onClick={() => onPageChange(page - 1)}
        className="flex h-9 w-9 items-center justify-center rounded-full text-gray-400 transition-colors hover:bg-green-light disabled:cursor-not-allowed disabled:opacity-40"
      >
        <ChevronLeft size={18} />
      </button>

      {pages.map((item, index) =>
        item === 'ellipsis' ? (
          <span key={`ellipsis-${index}`} className="px-1 text-gray-400">
            ...
          </span>
        ) : (
          <button
            key={item}
            type="button"
            onClick={() => onPageChange(item)}
            className={`flex h-9 w-9 items-center justify-center rounded-full text-sm font-medium transition-colors ${
              item === page ? 'bg-green-main text-white' : 'text-gray-600 hover:bg-green-light'
            }`}
          >
            {item}
          </button>
        ),
      )}

      <button
        type="button"
        disabled={page === totalPages}
        onClick={() => onPageChange(page + 1)}
        className="flex h-9 w-9 items-center justify-center rounded-full text-gray-400 transition-colors hover:bg-green-light disabled:cursor-not-allowed disabled:opacity-40"
      >
        <ChevronRight size={18} />
      </button>
    </div>
  )
}

export default Pagination
