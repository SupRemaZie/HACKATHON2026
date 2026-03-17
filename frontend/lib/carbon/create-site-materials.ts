import type { MaterialType } from "./types"

export type CreateMaterialCategory =
  | "Gros Œuvre & Structure"
  | "Enveloppe & Façade"
  | "Second Œuvre & Intérieurs"
  | "Aménagements Extérieurs"

export type CreateMaterial = Readonly<{
  id: string
  label: string
  unit: string
  category: CreateMaterialCategory
  mappedMaterialType: MaterialType
}>

export const CREATE_SITE_MATERIALS: ReadonlyArray<CreateMaterial> = [
  {
    id: "beton_pret_emploi",
    label: "Béton prêt à l'emploi",
    unit: "m³",
    category: "Gros Œuvre & Structure",
    mappedMaterialType: "concrete",
  },
  {
    id: "acier_construction",
    label: "Acier de construction / armatures",
    unit: "t",
    category: "Gros Œuvre & Structure",
    mappedMaterialType: "steel",
  },
  {
    id: "blocs_beton_parpaings",
    label: "Blocs de béton / parpaings",
    unit: "m²",
    category: "Gros Œuvre & Structure",
    mappedMaterialType: "concrete",
  },
  {
    id: "bois_charpente_lamelle",
    label: "Bois de charpente / lamellé-collé",
    unit: "m³",
    category: "Gros Œuvre & Structure",
    mappedMaterialType: "wood",
  },
  {
    id: "ciment",
    label: "Ciment",
    unit: "t",
    category: "Gros Œuvre & Structure",
    mappedMaterialType: "concrete",
  },
  {
    id: "verre_double_vitrage",
    label: "Verre / double vitrage",
    unit: "m²",
    category: "Enveloppe & Façade",
    mappedMaterialType: "glass",
  },
  {
    id: "laine_verre",
    label: "Laine de verre",
    unit: "m²",
    category: "Enveloppe & Façade",
    mappedMaterialType: "glass",
  },
  {
    id: "laine_roche",
    label: "Laine de roche",
    unit: "m²",
    category: "Enveloppe & Façade",
    mappedMaterialType: "glass",
  },
  {
    id: "polyurethane",
    label: "Polyuréthane (panneaux isolants)",
    unit: "m²",
    category: "Enveloppe & Façade",
    mappedMaterialType: "glass",
  },
  {
    id: "briques_facade",
    label: "Briques (façade)",
    unit: "m²",
    category: "Enveloppe & Façade",
    mappedMaterialType: "concrete",
  },
  {
    id: "bardage_metallique",
    label: "Bardage métallique",
    unit: "m²",
    category: "Enveloppe & Façade",
    mappedMaterialType: "steel",
  },
  {
    id: "plaques_platre",
    label: "Plaques de plâtre",
    unit: "m²",
    category: "Second Œuvre & Intérieurs",
    mappedMaterialType: "concrete",
  },
  {
    id: "moquette_dalles",
    label: "Moquette en dalles",
    unit: "m²",
    category: "Second Œuvre & Intérieurs",
    mappedMaterialType: "wood",
  },
  {
    id: "faux_plancher_technique",
    label: "Faux plancher technique",
    unit: "m²",
    category: "Second Œuvre & Intérieurs",
    mappedMaterialType: "steel",
  },
  {
    id: "dalles_faux_plafond",
    label: "Dalles de faux plafond",
    unit: "m²",
    category: "Second Œuvre & Intérieurs",
    mappedMaterialType: "concrete",
  },
  {
    id: "peinture_acrylique",
    label: "Peinture acrylique",
    unit: "m²",
    category: "Second Œuvre & Intérieurs",
    mappedMaterialType: "wood",
  },
  {
    id: "cablage_cuivre",
    label: "Câblage en cuivre",
    unit: "m",
    category: "Second Œuvre & Intérieurs",
    mappedMaterialType: "steel",
  },
  {
    id: "portes_bois_coupe_feu",
    label: "Portes en bois / coupe-feu",
    unit: "nb",
    category: "Second Œuvre & Intérieurs",
    mappedMaterialType: "wood",
  },
  {
    id: "enrobe_bitumineux",
    label: "Enrobé bitumineux / asphalte",
    unit: "m²",
    category: "Aménagements Extérieurs",
    mappedMaterialType: "concrete",
  },
  {
    id: "beton_drainant",
    label: "Béton drainant",
    unit: "m²",
    category: "Aménagements Extérieurs",
    mappedMaterialType: "concrete",
  },
  {
    id: "graviers_granulats",
    label: "Graviers / granulats",
    unit: "m²",
    category: "Aménagements Extérieurs",
    mappedMaterialType: "concrete",
  },
] as const

export const MATERIAL_CATEGORIES: ReadonlyArray<CreateMaterialCategory> = [
  "Gros Œuvre & Structure",
  "Enveloppe & Façade",
  "Second Œuvre & Intérieurs",
  "Aménagements Extérieurs",
]

