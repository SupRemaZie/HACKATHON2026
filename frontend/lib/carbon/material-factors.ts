import type { MaterialType } from './types'

export const EF_MATERIALS_KG_PER_TONNE: Readonly<Record<MaterialType, number>> = {
  concrete: 150,
  steel: 1_900,
  glass: 1_100,
  wood: 50,
} as const

