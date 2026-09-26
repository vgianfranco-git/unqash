import type { UsuarioRequestType } from '../services/UsuarioType'

export type UsuarioFormErrors = Partial<Record<Exclude<keyof UsuarioRequestType, 'esGestor'>, string>>
