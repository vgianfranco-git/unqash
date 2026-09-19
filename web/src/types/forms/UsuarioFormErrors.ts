import type { UsuarioRequestType } from '../services/UsuarioType'

export type UsuarioFormErrors = Partial<Record<keyof UsuarioRequestType, string>>
