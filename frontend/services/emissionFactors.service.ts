import { api } from "@/api/axios"
import { EmissionFactorDTO } from "@/types/emissionFactor"

export const getEmissionFactors = () => {
  return api.get<EmissionFactorDTO[]>("/emission-factors")
}

export const getEmissionFactorsByCategory = (category: string) => {
  return api.get<EmissionFactorDTO[]>("/emission-factors", {
    params: { category },
  })
}
