import { useEffect, useState } from 'react'
import Modal from '../ui/Modal'
import { facturaService } from '../../services/facturaService'
import { handleApiError } from '../../services/notifications'
import type { VerFotoModalProps } from '../../types/components/VerFotoModalProps'

function VerFotoModal({ facturaId, onClose }: VerFotoModalProps) {
  const [url, setUrl] = useState<string | null>(null)
  const [tipo, setTipo] = useState<string>('')
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    let objectUrl: string | null = null

    facturaService
      .obtenerFoto(facturaId)
      .then((blob) => {
        objectUrl = URL.createObjectURL(blob)
        setUrl(objectUrl)
        setTipo(blob.type)
      })
      .catch((err) => setError(handleApiError(err, 'No se pudo cargar el comprobante.')))
      .finally(() => setCargando(false))

    return () => {
      if (objectUrl) URL.revokeObjectURL(objectUrl)
    }
  }, [facturaId])

  return (
    <Modal title="Comprobante de la factura" widthClassName="max-w-2xl" onClose={onClose}>
      {cargando && <p className="py-10 text-center text-sm text-gray-500">Cargando...</p>}

      {!cargando && error && <p className="py-10 text-center text-sm text-red-500">{error}</p>}

      {!cargando && url && tipo === 'application/pdf' && (
        <embed src={url} type="application/pdf" className="h-[70vh] w-full rounded-xl" />
      )}

      {!cargando && url && tipo !== 'application/pdf' && (
        <img src={url} alt="Comprobante de la factura" className="max-h-[70vh] w-full rounded-xl object-contain" />
      )}
    </Modal>
  )
}

export default VerFotoModal
