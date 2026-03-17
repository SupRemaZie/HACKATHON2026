export type SiteId = 'rennes' | 'paris' | 'lyon'

export type MaterialType = 'concrete' | 'steel' | 'glass' | 'wood'

export type EnergySourceType = 'electricity' | 'gas'

export type EnergySource = Readonly<{
  type: EnergySourceType
  label: string
  annualMWh: number
}>

export type FootprintCategory =
  | 'construction_materials'
  | 'operations_energy'
  | 'parking'
  | 'other'

export type CategoryBreakdownKgCO2e = Readonly<Record<FootprintCategory, number>>

export type MaterialQuantity = Readonly<{
  material: MaterialType
  tonnes: number
}>

export type SiteSnapshot = Readonly<{
  year: number
  energySources: ReadonlyArray<EnergySource>
  employees: number
}>

export type Site = Readonly<{
  id: SiteId
  name: string
  areaM2: number
  parkingSpots: number
  employees: number
  energySources: ReadonlyArray<EnergySource>
  materials: ReadonlyArray<MaterialQuantity>
  history: ReadonlyArray<SiteSnapshot>
}>

export type FootprintResult = Readonly<{
  totalKgCO2e: number
  breakdownKgCO2e: CategoryBreakdownKgCO2e
}>

export type Kpis = Readonly<{
  total: Readonly<{ kgCO2e: number; tCO2e: number }>
  operations: Readonly<{ kgCO2e: number; tCO2e: number }>
  operationsByEnergy: ReadonlyArray<
    Readonly<{ type: EnergySourceType; label: string; kgCO2e: number; tCO2e: number }>
  >
  intensityPerM2: Readonly<{ kgCO2ePerM2: number }>
  intensityPerEmployee: Readonly<{ tCO2ePerEmployee: number }>
}>

export type YearPoint = Readonly<{
  label: string
  kgCO2e: number
}>

