import type {
  CategoryBreakdownKgCO2e,
  EnergySource,
  EnergySourceType,
  FootprintResult,
  Kpis,
  MaterialType,
  Site,
  YearPoint,
} from './types'
import { EF_MATERIALS_KG_PER_TONNE } from './material-factors'

type EmissionFactors = Readonly<{
  electricityKgCO2ePerKWh: number
  gasKgCO2ePerKWh: number
  parkingKgCO2ePerSpot: number
  otherFixedKgCO2e: number
}>

export type CalcOverrides = Readonly<{
  materialsKgCO2ePerTonne?: Partial<Record<MaterialType, number>>
  electricityKgCO2ePerKWh?: number
  gasKgCO2ePerKWh?: number
  parkingKgCO2ePerSpot?: number
  otherFixedKgCO2e?: number
}>

const EF: EmissionFactors = {
  electricityKgCO2ePerKWh: 0.055,
  gasKgCO2ePerKWh: 0.2,
  parkingKgCO2ePerSpot: 1_200,
  otherFixedKgCO2e: 80_000,
}

const toKWh = (mwh: number): number => mwh * 1_000
const toT = (kg: number): number => kg / 1_000

function mergeMaterialFactors(
  overrides?: CalcOverrides,
): Readonly<Record<MaterialType, number>> {
  return {
    concrete:
      overrides?.materialsKgCO2ePerTonne?.concrete ??
      EF_MATERIALS_KG_PER_TONNE.concrete,
    steel:
      overrides?.materialsKgCO2ePerTonne?.steel ?? EF_MATERIALS_KG_PER_TONNE.steel,
    glass:
      overrides?.materialsKgCO2ePerTonne?.glass ?? EF_MATERIALS_KG_PER_TONNE.glass,
    wood: overrides?.materialsKgCO2ePerTonne?.wood ?? EF_MATERIALS_KG_PER_TONNE.wood,
  }
}

function mergeEmissionFactors(overrides?: CalcOverrides): EmissionFactors {
  return {
    electricityKgCO2ePerKWh:
      overrides?.electricityKgCO2ePerKWh ?? EF.electricityKgCO2ePerKWh,
    gasKgCO2ePerKWh: overrides?.gasKgCO2ePerKWh ?? EF.gasKgCO2ePerKWh,
    parkingKgCO2ePerSpot: overrides?.parkingKgCO2ePerSpot ?? EF.parkingKgCO2ePerSpot,
    otherFixedKgCO2e: overrides?.otherFixedKgCO2e ?? EF.otherFixedKgCO2e,
  }
}

function energySourceKgCO2e(source: EnergySource, ef: EmissionFactors): number {
  const kWh = toKWh(source.annualMWh)
  if (source.type === 'electricity') {
    return kWh * ef.electricityKgCO2ePerKWh
  }
  if (source.type === 'gas') {
    return kWh * ef.gasKgCO2ePerKWh
  }
  return 0
}

function totalOperationsKgCO2e(
  energySources: ReadonlyArray<EnergySource>,
  ef: EmissionFactors,
): number {
  return energySources.reduce((sum, src) => sum + energySourceKgCO2e(src, ef), 0)
}

export function calcFootprintKgCO2e(
  site: Site,
  overrides?: CalcOverrides,
): FootprintResult {
  const materialFactors = mergeMaterialFactors(overrides)
  const ef = mergeEmissionFactors(overrides)

  const constructionMaterialsKg = site.materials.reduce((acc, m) => {
    const factor = materialFactors[m.material]
    return acc + m.tonnes * factor
  }, 0)

  const operationsEnergyKg = totalOperationsKgCO2e(site.energySources, ef)

  const parkingKg = site.parkingSpots * ef.parkingKgCO2ePerSpot
  const otherKg = ef.otherFixedKgCO2e

  const breakdown: CategoryBreakdownKgCO2e = {
    construction_materials: constructionMaterialsKg,
    operations_energy: operationsEnergyKg,
    parking: parkingKg,
    other: otherKg,
  }

  const totalKg = Object.values(breakdown).reduce((a, b) => a + b, 0)

  return { totalKgCO2e: totalKg, breakdownKgCO2e: breakdown }
}

export function calcKpis(site: Site, overrides?: CalcOverrides): Kpis {
  const footprint = calcFootprintKgCO2e(site, overrides)
  const operationsKg = footprint.breakdownKgCO2e.operations_energy
  const ef = mergeEmissionFactors(overrides)

  const operationsByEnergy: Kpis['operationsByEnergy'] = (['electricity', 'gas'] as const)
    .map((type: EnergySourceType) => {
      const sources = site.energySources.filter((s) => s.type === type)
      if (sources.length === 0) {
        return null
      }
      const label = sources[0]?.label ?? (type === 'electricity' ? 'Électricité' : 'Gaz')
      const kg = totalOperationsKgCO2e(sources, ef)
      return { type, label, kgCO2e: kg, tCO2e: toT(kg) } as const
    })
    .filter((entry): entry is NonNullable<typeof entry> => entry !== null)

  return {
    total: { kgCO2e: footprint.totalKgCO2e, tCO2e: toT(footprint.totalKgCO2e) },
    operations: { kgCO2e: operationsKg, tCO2e: toT(operationsKg) },
    operationsByEnergy,
    intensityPerM2: {
      kgCO2ePerM2: footprint.totalKgCO2e / Math.max(1, site.areaM2),
    },
    intensityPerEmployee: {
      tCO2ePerEmployee: toT(footprint.totalKgCO2e) / Math.max(1, site.employees),
    },
  }
}

export function calcYearlyKgCO2e(site: Site): ReadonlyArray<YearPoint> {
  const ef = mergeEmissionFactors()
  return site.history.map((snap) => {
    const opsKg = totalOperationsKgCO2e(snap.energySources, ef)
    const baseline = calcFootprintKgCO2e(site)
    const baseOpsKg = baseline.breakdownKgCO2e.operations_energy
    const adjustedTotalKg = baseline.totalKgCO2e - baseOpsKg + opsKg
    return { label: snap.year.toString(), kgCO2e: adjustedTotalKg }
  })
}

