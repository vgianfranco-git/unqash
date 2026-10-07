import { useEffect, useState } from 'react'
import FacturaCard from './FacturaCard'
import EditarFacturaModal from './EditarFacturaModal'
import VerFotoModal from './VerFotoModal'
import Pagination from '../ui/Pagination'
import ConfirmModal from '../ui/ConfirmModal'
import { facturaService } from '../../services/facturaService'
import { notificationService, handleApiError } from '../../services/notifications'
import type { FacturaHistorialType } from '../../types/services/FacturaType'
import type { ListadoFacturasProps } from '../../types/components/ListadoFacturasProps'

const LARGO_MAX_PROVEEDOR_MODAL = 10

function truncar(texto: string, largoMax: number): string {
    return texto.length > largoMax ? `${texto.slice(0, largoMax)}...` : texto
}

function ListadoFacturas({ refreshKey }: ListadoFacturasProps) {
    const [page, setPage] = useState(1)
    const [facturas, setFacturas] = useState<FacturaHistorialType[]>([])
    const [totalPages, setTotalPages] = useState(1)
    const [cargando, setCargando] = useState(true)
    const [recargaInterna, setRecargaInterna] = useState(0)

    const [facturaAEditar, setFacturaAEditar] = useState<string | null>(null)
    const [facturaAVer, setFacturaAVer] = useState<string | null>(null)
    const [facturaAAnular, setFacturaAAnular] = useState<FacturaHistorialType | null>(null)
    const [anulando, setAnulando] = useState(false)

    useEffect(() => {
        setCargando(true)
        facturaService
            .listar(page)
            .then((respuesta) => {
                setFacturas(respuesta.facturas)
                setTotalPages(respuesta.totalPag)
            })
            .catch((error) => notificationService.error(handleApiError(error, 'No se pudo cargar el historial de facturas.')))
            .finally(() => setCargando(false))
    }, [page, refreshKey, recargaInterna])

    const handleAnular = async () => {
        if (!facturaAAnular) return

        setAnulando(true)
        try {
            await facturaService.anular(facturaAAnular.id)
            notificationService.success('Factura anulada con éxito.')
            setFacturaAAnular(null)
            setRecargaInterna((key) => key + 1)
        } catch (error) {
            notificationService.error(handleApiError(error, 'No se pudo anular la factura.'))
        } finally {
            setAnulando(false)
        }
    }

    return (
        <div className="flex flex-col gap-3">
            <h3 className="text-sm font-semibold uppercase text-gray-500">Historial de facturas</h3>

            {cargando ? (
                <p className="text-sm text-gray-500">Cargando...</p>
            ) : facturas.length === 0 ? (
                <p className="text-center text-sm text-gray-500">Todavía no hay facturas registradas.</p>
            ) : (
                facturas.map((factura) => (
                    <FacturaCard
                        key={factura.id}
                        id={factura.id}
                        proveedor={factura.proveedor}
                        detalles={factura.detalle}
                        fecha={factura.fecha}
                        usuario={factura.usuarioCarga}
                        monto={factura.monto}
                        tieneFoto={factura.tieneFoto}
                        onEditar={setFacturaAEditar}
                        onVerFoto={setFacturaAVer}
                        onAnular={() => setFacturaAAnular(factura)}
                    />
                ))
            )}

            {totalPages > 1 && <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />}

            {facturaAEditar && (
                <EditarFacturaModal
                    facturaId={facturaAEditar}
                    onClose={() => setFacturaAEditar(null)}
                    onFacturaActualizada={() => setRecargaInterna((key) => key + 1)}
                />
            )}

            {facturaAVer && <VerFotoModal facturaId={facturaAVer} onClose={() => setFacturaAVer(null)} />}

            {facturaAAnular && (
                <ConfirmModal
                    title={`¿Desea anular la factura de "${truncar(facturaAAnular.proveedor, LARGO_MAX_PROVEEDOR_MODAL)}"?`}
                    subtitle="Esta acción no se puede deshacer."
                    isConfirming={anulando}
                    onConfirm={handleAnular}
                    onClose={() => setFacturaAAnular(null)}
                />
            )}
        </div>
    )
}

export default ListadoFacturas
