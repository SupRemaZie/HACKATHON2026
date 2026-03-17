"use client"

import { useLayoutEffect, useMemo, useRef, useState } from "react"
import Link from "next/link"
import { HugeiconsIcon } from "@hugeicons/react"
import { Cancel01Icon } from "@hugeicons/core-free-icons"

import { calcFootprintKgCO2e, calcKpis, type CalcOverrides } from "@/lib/carbon/calc"
import {
  CREATE_SITE_MATERIALS,
  MATERIAL_CATEGORIES,
  type CreateMaterialCategory,
} from "@/lib/carbon/create-site-materials"
import type { MaterialType, Site } from "@/lib/carbon/types"
import { Button } from "@/components/ui/button"
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card"
import {
  Field,
  FieldDescription,
  FieldGroup,
  FieldLabel,
} from "@/components/ui/field"
import { InputGroup, InputGroupInput } from "@/components/ui/input-group"

// 4 steps — Facteurs d'émission removed
type WizardStep = 1 | 2 | 3 | 4

type CreateSiteDraft = Readonly<{
  siteName: string
  location: string
  areaM2: number
  parkingSpots: number
  electricityMWh: number
  gasMWh: number
  employees: number
  materialQuantities: Readonly<Record<string, number>>
  energyFactors: Readonly<{
    electricityKgCO2ePerKWh: number
    gasKgCO2ePerKWh: number
  }>
  materialFactors: Readonly<Record<MaterialType, number>>
}>

const STEP_ITEMS: ReadonlyArray<Readonly<{ step: WizardStep; title: string }>> = [
  { step: 1, title: "Nom & lieu" },
  { step: 2, title: "Informations du site" },
  { step: 3, title: "Matériaux + quantités" },
  { step: 4, title: "Résumé" },
]

const STEP_DESCRIPTIONS: Readonly<Record<WizardStep, string>> = {
  1: "Renseignez l'identité du site.",
  2: "Ajoutez les données structurantes du site.",
  3: "Saisissez les quantités de matériaux.",
  4: "Vérifiez le résumé avant création.",
}

const DEFAULT_DRAFT: CreateSiteDraft = {
  siteName: "",
  location: "",
  areaM2: 0,
  parkingSpots: 0,
  electricityMWh: 0,
  gasMWh: 0,
  employees: 0,
  materialQuantities: Object.fromEntries(
    CREATE_SITE_MATERIALS.map((m) => [m.id, 0]),
  ),
  energyFactors: {
    electricityKgCO2ePerKWh: 0.055,
    gasKgCO2ePerKWh: 0.2,
  },
  materialFactors: {
    concrete: 150,
    steel: 1900,
    glass: 1100,
    wood: 50,
  },
}

function parseNumber(input: string): number {
  const parsed = Number.parseFloat(input.trim().replace(",", "."))
  return Number.isFinite(parsed) ? Math.max(0, parsed) : 0
}

function aggregateMaterialsToSiteMaterials(draft: CreateSiteDraft): Site["materials"] {
  const totals: Record<MaterialType, number> = { concrete: 0, steel: 0, glass: 0, wood: 0 }
  CREATE_SITE_MATERIALS.forEach((m) => {
    totals[m.mappedMaterialType] += draft.materialQuantities[m.id] ?? 0
  })
  return (Object.keys(totals) as MaterialType[]).map((t) => ({ material: t, tonnes: totals[t] }))
}

function buildPreviewSite(draft: CreateSiteDraft): Site {
  return {
    id: "rennes",
    name: draft.siteName || "Nouveau site",
    areaM2: draft.areaM2,
    parkingSpots: draft.parkingSpots,
    employees: draft.employees,
    energySources: [
      { type: "electricity", label: "Électricité", annualMWh: draft.electricityMWh },
      { type: "gas", label: "Gaz", annualMWh: draft.gasMWh },
    ],
    materials: aggregateMaterialsToSiteMaterials(draft),
    history: [{ year: 2025, employees: draft.employees, energySources: [] }],
  }
}

// ─── Card stack animation ────────────────────────────────────────────────────

const PEEK_HEIGHT = 76 // px of previous card visible at the top
const CARD_GAP = 28 // px between stacked cards

function getCardStyle(
  cardIndex: number,
  activeIndex: number,
  heights: number[],
): React.CSSProperties {
  const diff = cardIndex - activeIndex
  const activeHeight = heights[activeIndex] ?? 400
  const thisHeight = heights[cardIndex] ?? 400

  if (diff === 0) {
    return {
      transform: `translateY(${PEEK_HEIGHT + CARD_GAP}px)`,
      opacity: 1,
      zIndex: 10,
      pointerEvents: "auto",
    }
  }
  if (diff === -1) {
    // Previous card: bottom PEEK_HEIGHT px visible, faded and scaled
    return {
      transform: `translateY(${PEEK_HEIGHT - thisHeight - CARD_GAP}px) scale(0.95)`,
      opacity: 0.5,
      zIndex: 5,
      pointerEvents: "none",
    }
  }
  if (diff < -1) {
    return {
      transform: `translateY(${-(activeHeight + thisHeight + 80)}px)`,
      opacity: 0,
      zIndex: 1,
      pointerEvents: "none",
    }
  }
  // Future cards: hidden below
  return {
    transform: `translateY(${PEEK_HEIGHT + CARD_GAP + activeHeight + 32}px)`,
    opacity: 0,
    zIndex: 1,
    pointerEvents: "none",
  }
}

// ─── Page ────────────────────────────────────────────────────────────────────

export default function CreatePage() {
  const [wizardStep, setWizardStep] = useState<WizardStep>(1)
  const [draft, setDraft] = useState<CreateSiteDraft>(DEFAULT_DRAFT)

  const cardRefs = useRef<Array<HTMLDivElement | null>>([null, null, null, null])
  const [cardHeights, setCardHeights] = useState<number[]>([400, 400, 400, 400])

  useLayoutEffect(() => {
    const measure = () =>
      setCardHeights(cardRefs.current.map((r) => r?.offsetHeight ?? 400))
    measure()
    const ro = new ResizeObserver(measure)
    cardRefs.current.forEach((r) => r && ro.observe(r))
    return () => ro.disconnect()
  }, [])

  const activeIndex = wizardStep - 1
  const containerHeight = PEEK_HEIGHT + CARD_GAP + (cardHeights[activeIndex] ?? 400)

  // ── Carbon preview ──────────────────────────────────────────────────────────

  const previewSite = useMemo(() => buildPreviewSite(draft), [draft])
  const previewOverrides = useMemo<CalcOverrides>(
    () => ({
      electricityKgCO2ePerKWh: draft.energyFactors.electricityKgCO2ePerKWh,
      gasKgCO2ePerKWh: draft.energyFactors.gasKgCO2ePerKWh,
      materialsKgCO2ePerTonne: draft.materialFactors,
    }),
    [draft.energyFactors, draft.materialFactors],
  )
  const previewFootprint = useMemo(
    () => calcFootprintKgCO2e(previewSite, previewOverrides),
    [previewSite, previewOverrides],
  )
  const previewKpis = useMemo(
    () => calcKpis(previewSite, previewOverrides),
    [previewSite, previewOverrides],
  )

  const totalEnergyMWh = draft.electricityMWh + draft.gasMWh
  const materialRows = CREATE_SITE_MATERIALS.map((m) => ({
    ...m,
    quantity: draft.materialQuantities[m.id] ?? 0,
  }))

  // ── Validation ──────────────────────────────────────────────────────────────

  const isStepValid = (step: WizardStep): boolean => {
    if (step === 1) return draft.siteName.trim().length > 1 && draft.location.trim().length > 1
    if (step === 2) return draft.areaM2 > 0 && draft.employees > 0 && totalEnergyMWh > 0
    if (step === 3) return materialRows.some((r) => r.quantity > 0)
    return true
  }

  // ── Handlers ────────────────────────────────────────────────────────────────

  const handleNext = () => {
    if (!isStepValid(wizardStep)) return
    setWizardStep((p) => Math.min(4, p + 1) as WizardStep)
  }

  const handlePrev = () => {
    setWizardStep((p) => Math.max(1, p - 1) as WizardStep)
  }

  const handleApplyPreset = () =>
    setDraft({
      ...DEFAULT_DRAFT,
      siteName: "Rennes Campus",
      location: "Rennes",
      areaM2: 11_771,
      parkingSpots: 520,
      electricityMWh: 1_500,
      gasMWh: 340,
      employees: 1_800,
      materialQuantities: {
        ...DEFAULT_DRAFT.materialQuantities,
        beton_pret_emploi: 22000,
        acier_construction: 1650,
        verre_double_vitrage: 420,
        bois_charpente_lamelle: 260,
      },
    })

  const handleMaterialQty = (id: string, value: string) =>
    setDraft((p) => ({
      ...p,
      materialQuantities: { ...p.materialQuantities, [id]: parseNumber(value) },
    }))

  const handleCreateSite = () => {
    if (!isStepValid(4)) return

    // TODO: handle api call to create site
  }

  // ── Shared footer ───────────────────────────────────────────────────────────

  const stepFooter = (step: WizardStep) => (
    <CardFooter className="shrink-0 justify-between">
      <Button variant="outline" onClick={handlePrev} disabled={step === 1}>
        Retour
      </Button>
      {step < 4 ? (
        <Button onClick={handleNext} disabled={!isStepValid(step)}>
          Continuer
        </Button>
      ) : (
        <Button disabled={!isStepValid(4)} onClick={handleCreateSite}>Créer le site (mock)</Button>
      )}
    </CardFooter>
  )

  // ── Render ──────────────────────────────────────────────────────────────────

  return (
    <main className="relative flex min-h-svh items-start justify-center px-6 pb-6">
      <div className="relative grid w-full max-w-6xl items-start gap-6 lg:grid-cols-[160px_minmax(0,1fr)_220px]">

        <div className="hidden text-sm font-medium text-muted-foreground lg:block" style={{ paddingTop: PEEK_HEIGHT + CARD_GAP }}>
          Créer un site
        </div>

        <Button variant="ghost" size="icon" className="absolute top-4 right-0" asChild>
          <Link href="/">
            <HugeiconsIcon icon={Cancel01Icon} strokeWidth={2} />
            <span className="sr-only">Fermer</span>
          </Link>
        </Button>

        {/* ── Card stack ─────────────────────────────────────────────────────── */}
        {/* Outer wrapper gives breathing room so card borders aren't clipped. */}
        <div className="relative p-2">
          <div
            style={{
              position: "relative",
              overflow: "hidden",
              height: containerHeight,
              transition: "height 0.45s cubic-bezier(0.4, 0, 0.2, 1)",
            }}
          >
          {/* ── Step 1: Nom & lieu ─────────────────────────────────────────── */}
          <div
            ref={(el) => { cardRefs.current[0] = el }}
            style={{
              position: "absolute", top: 0, left: 0, right: 0,
              transition: "transform 0.45s cubic-bezier(0.4, 0, 0.2, 1), opacity 0.35s ease",
              ...getCardStyle(0, activeIndex, cardHeights),
            }}
          >
            <Card className="flex max-h-[70svh] w-full flex-col overflow-hidden">
              <CardHeader className="shrink-0 flex flex-row items-start justify-between gap-4">
                <div>
                  <CardTitle>{STEP_ITEMS[0].title}</CardTitle>
                  <CardDescription>{STEP_DESCRIPTIONS[1]}</CardDescription>
                </div>
                <Button variant="ghost" size="sm" onClick={handleApplyPreset}>
                  Pré-remplir
                </Button>
              </CardHeader>
              <CardContent className="min-h-0 flex-1 overflow-y-auto">
                <FieldGroup className="grid gap-4">
                  <Field>
                    <FieldLabel htmlFor="s1-name">Nom du site</FieldLabel>
                    <InputGroup>
                      <InputGroupInput
                        id="s1-name"
                        value={draft.siteName}
                        onChange={(e) => setDraft((p) => ({ ...p, siteName: e.currentTarget.value }))}
                        placeholder="Campus Rennes"
                      />
                    </InputGroup>
                  </Field>
                  <Field>
                    <FieldLabel htmlFor="s1-location">Lieu</FieldLabel>
                    <InputGroup>
                      <InputGroupInput
                        id="s1-location"
                        value={draft.location}
                        onChange={(e) => setDraft((p) => ({ ...p, location: e.currentTarget.value }))}
                        placeholder="Rennes"
                      />
                    </InputGroup>
                  </Field>
                </FieldGroup>
              </CardContent>
              {stepFooter(1)}
            </Card>
          </div>

          {/* ── Step 2: Informations du site ───────────────────────────────── */}
          <div
            ref={(el) => { cardRefs.current[1] = el }}
            style={{
              position: "absolute", top: 0, left: 0, right: 0,
              transition: "transform 0.45s cubic-bezier(0.4, 0, 0.2, 1), opacity 0.35s ease",
              ...getCardStyle(1, activeIndex, cardHeights),
            }}
          >
            <Card className="flex max-h-[70svh] w-full flex-col overflow-hidden">
              <CardHeader className="shrink-0">
                <CardTitle>{STEP_ITEMS[1].title}</CardTitle>
                <CardDescription>{STEP_DESCRIPTIONS[2]}</CardDescription>
              </CardHeader>
              <CardContent className="min-h-0 flex-1 overflow-y-auto">
                <FieldGroup className="grid gap-4 md:grid-cols-2">
                  <Field>
                    <FieldLabel htmlFor="s2-area">Surface totale (m²)</FieldLabel>
                    <InputGroup>
                      <InputGroupInput
                        id="s2-area"
                        inputMode="decimal"
                        value={draft.areaM2 ? String(draft.areaM2) : ""}
                        onChange={(e) => setDraft((p) => ({ ...p, areaM2: parseNumber(e.currentTarget.value) }))}
                      />
                    </InputGroup>
                  </Field>
                  <Field>
                    <FieldLabel htmlFor="s2-parking">Places de parking</FieldLabel>
                    <InputGroup>
                      <InputGroupInput
                        id="s2-parking"
                        inputMode="decimal"
                        value={draft.parkingSpots ? String(draft.parkingSpots) : ""}
                        onChange={(e) => setDraft((p) => ({ ...p, parkingSpots: parseNumber(e.currentTarget.value) }))}
                      />
                    </InputGroup>
                  </Field>
                  <Field>
                    <FieldLabel htmlFor="s2-elec">Électricité annuelle (MWh)</FieldLabel>
                    <InputGroup>
                      <InputGroupInput
                        id="s2-elec"
                        inputMode="decimal"
                        value={draft.electricityMWh ? String(draft.electricityMWh) : ""}
                        onChange={(e) => setDraft((p) => ({ ...p, electricityMWh: parseNumber(e.currentTarget.value) }))}
                      />
                    </InputGroup>
                  </Field>
                  <Field>
                    <FieldLabel htmlFor="s2-gas">Gaz annuel (MWh)</FieldLabel>
                    <InputGroup>
                      <InputGroupInput
                        id="s2-gas"
                        inputMode="decimal"
                        value={draft.gasMWh ? String(draft.gasMWh) : ""}
                        onChange={(e) => setDraft((p) => ({ ...p, gasMWh: parseNumber(e.currentTarget.value) }))}
                      />
                    </InputGroup>
                  </Field>
                  <Field>
                    <FieldLabel htmlFor="s2-employees">Nombre d&apos;employés</FieldLabel>
                    <InputGroup>
                      <InputGroupInput
                        id="s2-employees"
                        inputMode="decimal"
                        value={draft.employees ? String(draft.employees) : ""}
                        onChange={(e) => setDraft((p) => ({ ...p, employees: parseNumber(e.currentTarget.value) }))}
                      />
                    </InputGroup>
                  </Field>
                  <Field>
                    <FieldLabel htmlFor="s2-total">Conso totale (MWh)</FieldLabel>
                    <InputGroup>
                      <InputGroupInput id="s2-total" readOnly value={totalEnergyMWh.toFixed(1)} />
                    </InputGroup>
                    <FieldDescription>Somme électricité + gaz.</FieldDescription>
                  </Field>
                </FieldGroup>
              </CardContent>
              {stepFooter(2)}
            </Card>
          </div>

          {/* ── Step 3: Matériaux + quantités ──────────────────────────────── */}
          <div
            ref={(el) => { cardRefs.current[2] = el }}
            style={{
              position: "absolute", top: 0, left: 0, right: 0,
              transition: "transform 0.45s cubic-bezier(0.4, 0, 0.2, 1), opacity 0.35s ease",
              ...getCardStyle(2, activeIndex, cardHeights),
            }}
          >
            <Card className="flex max-h-[70svh] w-full flex-col overflow-hidden">
              <CardHeader className="shrink-0">
                <CardTitle>{STEP_ITEMS[2].title}</CardTitle>
                <CardDescription>{STEP_DESCRIPTIONS[3]}</CardDescription>
              </CardHeader>
              <CardContent className="min-h-0 flex-1 overflow-y-auto">
                <div className="flex flex-col gap-4">
                  {MATERIAL_CATEGORIES.map((cat) => (
                    <MaterialCategorySection
                      key={cat}
                      category={cat}
                      rows={materialRows.filter((r) => r.category === cat)}
                      onChange={handleMaterialQty}
                    />
                  ))}
                </div>
              </CardContent>
              {stepFooter(3)}
            </Card>
          </div>

          {/* ── Step 4: Résumé ─────────────────────────────────────────────── */}
          <div
            ref={(el) => { cardRefs.current[3] = el }}
            style={{
              position: "absolute", top: 0, left: 0, right: 0,
              transition: "transform 0.45s cubic-bezier(0.4, 0, 0.2, 1), opacity 0.35s ease",
              ...getCardStyle(3, activeIndex, cardHeights),
            }}
          >
            <Card className="flex max-h-[70svh] w-full flex-col overflow-hidden">
              <CardHeader className="shrink-0">
                <CardTitle>{STEP_ITEMS[3].title}</CardTitle>
                <CardDescription>{STEP_DESCRIPTIONS[4]}</CardDescription>
              </CardHeader>
              <CardContent className="min-h-0 flex-1 overflow-y-auto pt-6">
                <div className="flex flex-col gap-4">
                  <div className="grid gap-3 md:grid-cols-3">
                    <SummaryMetricCard
                      title="Construction"
                      value={`${(previewFootprint.breakdownKgCO2e.construction_materials / 1000).toFixed(1)} tCO₂e`}
                    />
                    <SummaryMetricCard
                      title="Exploitation énergie"
                      value={`${previewKpis.operations.tCO2e.toFixed(1)} tCO₂e`}
                    />
                    <SummaryMetricCard
                      title="Total estimé"
                      value={`${previewKpis.total.tCO2e.toFixed(1)} tCO₂e`}
                    />
                  </div>
                  <Card>
                    <CardHeader>
                      <CardTitle className="text-sm">Récapitulatif site</CardTitle>
                    </CardHeader>
                    <CardContent className="grid gap-2 text-sm">
                      <SummaryRow label="Nom du site" value={draft.siteName || "—"} />
                      <SummaryRow label="Lieu" value={draft.location || "—"} />
                      <SummaryRow label="Surface" value={`${draft.areaM2.toLocaleString()} m²`} />
                      <SummaryRow label="Parking" value={`${draft.parkingSpots.toLocaleString()} places`} />
                      <SummaryRow label="Énergie totale" value={`${totalEnergyMWh.toFixed(1)} MWh/an`} />
                      <SummaryRow label="Employés" value={draft.employees.toLocaleString()} />
                    </CardContent>
                  </Card>
                </div>
              </CardContent>
              {stepFooter(4)}
            </Card>
          </div>
        </div>
        </div>

        {/* ── Step list ───────────────────────────────────────────────────────── */}
        <ol
          className="hidden lg:flex list-none flex-col gap-px text-sm"
          style={{ paddingTop: PEEK_HEIGHT + CARD_GAP }}
        >
          {STEP_ITEMS.map((item) => {
            return (
              <li key={item.step}>
                <Button
                  variant="ghost"
                  onClick={() => {
                    if (item.step <= wizardStep) setWizardStep(item.step)
                  }}
                  disabled={item.step > wizardStep}
                >
                  • {item.title}
                </Button>
              </li>
            )
          })}
        </ol>
      </div>
    </main>
  )
}

// ─── Sub-components ──────────────────────────────────────────────────────────

function MaterialCategorySection(props: Readonly<{
  category: CreateMaterialCategory
  rows: ReadonlyArray<Readonly<{ id: string; label: string; unit: string; quantity: number }>>
  onChange: (id: string, value: string) => void
}>) {
  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-sm">{props.category}</CardTitle>
      </CardHeader>
      <CardContent className="grid gap-3 md:grid-cols-2">
        {props.rows.map((row) => (
          <Field key={row.id}>
            <FieldLabel htmlFor={`mat-${row.id}`}>{row.label}</FieldLabel>
            <InputGroup>
              <InputGroupInput
                id={`mat-${row.id}`}
                inputMode="decimal"
                value={row.quantity ? String(row.quantity) : ""}
                onChange={(e) => props.onChange(row.id, e.currentTarget.value)}
              />
            </InputGroup>
            <FieldDescription>Unité : {row.unit}</FieldDescription>
          </Field>
        ))}
      </CardContent>
    </Card>
  )
}

function SummaryMetricCard(props: Readonly<{ title: string; value: string }>) {
  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-xs font-semibold uppercase text-muted-foreground">
          {props.title}
        </CardTitle>
      </CardHeader>
      <CardContent className="text-xl font-semibold">{props.value}</CardContent>
    </Card>
  )
}

function SummaryRow(props: Readonly<{ label: string; value: string }>) {
  return (
    <div className="flex items-center justify-between gap-3">
      <span className="text-muted-foreground">{props.label}</span>
      <span className="font-medium">{props.value}</span>
    </div>
  )
}
