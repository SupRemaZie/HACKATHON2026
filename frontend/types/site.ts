export type SiteResponseDTO = {
  id: number
  token: string
  name: string
  address: string | null
  city: string | null
  surfaceM2: number | null
  nbEmployees: number
  nbWorkstations: number
  parkingUnderground: number
  parkingBasement: number
  parkingOutdoor: number
  createdAt: string
  updatedAt: string
}

export type SiteMaterialDTO = {
  id: number
  siteId: number
  emissionFactorId: number
  quantityKg: number
  co2KgStored: number
}

export type SiteDashboardDTO = {
  site: SiteResponseDTO
  siteMaterials: SiteMaterialDTO[]
  energyKwhYear?: number
}
