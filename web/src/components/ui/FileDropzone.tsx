import { useEffect, useRef, useState } from 'react'
import { FileText, Upload, X } from 'lucide-react'
import { formatFileSize } from '../../utils/fileSize'
import type { FileDropzoneProps } from '../../types/components/FileDropzoneProps'

function FileDropzone({ id, label, accept, hint, file, error, disabled, onFileChange }: FileDropzoneProps) {
  const inputRef = useRef<HTMLInputElement>(null)
  const [arrastrando, setArrastrando] = useState(false)
  const [previewUrl, setPreviewUrl] = useState<string | null>(null)

  useEffect(() => {
    if (!file || !file.type.startsWith('image/')) {
      setPreviewUrl(null)
      return
    }
    const url = URL.createObjectURL(file)
    setPreviewUrl(url)
    return () => URL.revokeObjectURL(url)
  }, [file])

  const seleccionar = (files: FileList | null) => {
    if (disabled || !files || files.length === 0) return
    onFileChange(files[0])
  }

  return (
    <div className="flex h-full flex-col">
      <label htmlFor={id} className="mb-1 block text-xs font-semibold uppercase text-gray-500">
        {label}
      </label>

      <input
        ref={inputRef}
        id={id}
        type="file"
        accept={accept}
        disabled={disabled}
        className="hidden"
        onChange={(e) => {
          seleccionar(e.target.files)
          // Permite volver a elegir el mismo archivo después de quitarlo
          e.target.value = ''
        }}
      />

      {file ? (
        <div
          className={`flex items-center justify-between gap-3 rounded-xl border bg-white p-2 ${
            error ? 'border-red-300' : 'border-gray-200'
          }`}
        >
          <div className="flex min-w-0 items-center gap-3">
            {previewUrl ? (
              <img src={previewUrl} alt={file.name} className="h-12 w-12 shrink-0 rounded-lg object-cover" />
            ) : (
              <span className="flex h-12 w-12 shrink-0 items-center justify-center rounded-lg bg-green-light text-green-main">
                <FileText size={20} />
              </span>
            )}
            <div className="min-w-0">
              <p className="truncate text-sm text-gray-800">{file.name}</p>
              <p className="text-xs text-gray-400">{formatFileSize(file.size)}</p>
            </div>
          </div>
          <button
            type="button"
            aria-label="Quitar archivo"
            disabled={disabled}
            onClick={() => onFileChange(null)}
            className="flex h-7 w-7 shrink-0 items-center justify-center rounded-full text-gray-500 transition-colors hover:bg-gray-100 disabled:cursor-not-allowed disabled:opacity-60"
          >
            <X size={14} />
          </button>
        </div>
      ) : (
        <div
          role="button"
          tabIndex={disabled ? -1 : 0}
          onClick={() => !disabled && inputRef.current?.click()}
          onKeyDown={(e) => {
            if (!disabled && (e.key === 'Enter' || e.key === ' ')) {
              e.preventDefault()
              inputRef.current?.click()
            }
          }}
          onDragOver={(e) => {
            e.preventDefault()
            if (!disabled) setArrastrando(true)
          }}
          onDragLeave={() => setArrastrando(false)}
          onDrop={(e) => {
            e.preventDefault()
            setArrastrando(false)
            seleccionar(e.dataTransfer.files)
          }}
          className={`flex min-h-40 flex-1 flex-col items-center justify-center gap-2 rounded-xl border border-dashed p-6 text-center transition-colors ${
            arrastrando ? 'border-green-main bg-green-light' : 'border-green-medium bg-background'
          } ${disabled ? 'cursor-not-allowed opacity-60' : 'cursor-pointer hover:bg-green-light'}`}
        >
          <Upload size={22} className="text-gray-600" />
          <p className="text-sm font-semibold text-gray-800">Tocá o arrastrá para cargar un archivo</p>
          <p className="text-xs text-gray-400">{hint}</p>
        </div>
      )}

      {error && <p className="mt-1 text-xs text-red-500">{error}</p>}
    </div>
  )
}

export default FileDropzone
