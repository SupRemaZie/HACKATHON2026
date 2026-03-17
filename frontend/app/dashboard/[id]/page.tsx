"use client"

import React from "react"
import Link from "next/link"
import { useParams } from "next/navigation"
import { useEffect, useMemo, useState } from "react"
import type { AxiosError } from "axios"
import { Bar, BarChart, Cell, Pie, PieChart, XAxis, YAxis } from "recharts"

import { api } from "@/api/axios"
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import {
  Card,
  CardAction,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card"
import {
  ChartContainer,
  ChartLegend,
  ChartLegendContent,
  ChartTooltip,
  ChartTooltipContent,
  type ChartConfig,
} from "@/components/ui/chart"
import { Separator } from "@/components/ui/separator"
import { Skeleton } from "@/components/ui/skeleton"
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table"

import type { EmissionFactorDTO } from "@/types/emissionFactor"
import { SiteDashboardDTO, SiteMaterialDTO } from "@/types/site"

type Loadable<T> =
  | { kind: "loading" }
  | { kind: "error"; message: string }
  | { kind: "ready"; data: T }

function mockFetchSiteDashboard(idOrToken: string): Promise<SiteDashboardDTO> {
  const payload: SiteDashboardDTO = {
    site: {
      id: 12,
      token: idOrToken,
      name: "Capgemini Rennes",
      address: "Avenue de Joinville",
      city: "Rennes",
      surfaceM2: 11771,
      nbEmployees: 1800,
      nbWorkstations: 1037,
      parkingUnderground: 41,
      parkingBasement: 184,
      parkingOutdoor: 83,
      createdAt: "2026-03-01T10:22:11",
      updatedAt: "2026-03-10T17:05:42",
    },
    siteMaterials: [
      { id: 501, siteId: 12, emissionFactorId: 1, quantityKg: 250000, co2KgStored: 0 },
      { id: 502, siteId: 12, emissionFactorId: 2, quantityKg: 12000, co2KgStored: 0 },
      { id: 503, siteId: 12, emissionFactorId: 4, quantityKg: 8000, co2KgStored: 0 },
    ],
    energyKwhYear: 1_500_000,
  }

  return Promise.resolve(payload)
}

function safeDiv(numerator: number, denominator: number | null): number | null {
  if (typeof denominator !== "number" || !Number.isFinite(denominator) || denominator === 0) {
    return null
  }
  return numerator / denominator
}

const nf = new Intl.NumberFormat("fr-FR", { maximumFractionDigits: 0 })
const nf2 = new Intl.NumberFormat("fr-FR", { maximumFractionDigits: 2 })

function formatKg(valueKg: number | null): string {
  if (typeof valueKg !== "number" || !Number.isFinite(valueKg)) return "—"
  if (valueKg >= 1_000_000) return `${nf2.format(valueKg / 1_000_000)} MtCO₂`
  if (valueKg >= 1_000) return `${nf2.format(valueKg / 1_000)} tCO₂`
  return `${nf.format(valueKg)} kgCO₂`
}

function formatKwhYear(valueKwh: number | null | undefined): string {
  if (typeof valueKwh !== "number" || !Number.isFinite(valueKwh)) return "—"
  if (valueKwh >= 1_000_000) return `${nf2.format(valueKwh / 1_000_000)} GWh/an`
  if (valueKwh >= 1_000) return `${nf2.format(valueKwh / 1_000)} MWh/an`
  return `${nf.format(valueKwh)} kWh/an`
}

function formatKgPerUnit(value: number | null): string {
  if (typeof value !== "number" || !Number.isFinite(value)) return "—"
  if (value >= 1_000) return `${nf2.format(value / 1_000)} tCO₂`
  return `${nf2.format(value)} kgCO₂`
}

/** Sanitize a string for use as a CSS custom property key */
function toChartKey(name: string): string {
  return name.toLowerCase().replace(/[^a-z0-9]+/g, "-")
}

export default function DashboardSitePage() {
  const params = useParams<{ id: string }>()
  const id = params.id

  const [dashboardState, setDashboardState] = useState<Loadable<SiteDashboardDTO>>({
    kind: "loading",
  })
  const [factorsState, setFactorsState] = useState<Loadable<EmissionFactorDTO[]>>({
    kind: "loading",
  })

  useEffect(() => {
    let cancelled = false

    async function loadDashboard() {
      setDashboardState({ kind: "loading" })
      try {
        // TODO: Replace with real call once endpoint exists:
        // const response = await api.get<SiteDashboardDTO>(`/sites/${id}/dashboard`)
        // if (!cancelled) setDashboardState({ kind: "ready", data: response.data })
        const data = await mockFetchSiteDashboard(id)
        if (!cancelled) setDashboardState({ kind: "ready", data })
      } catch (e: unknown) {
        if (cancelled) return
        const axiosError = e as AxiosError<{ message?: string }>
        const message =
          axiosError.response?.data?.message ??
          axiosError.message ??
          "Erreur lors du chargement du dashboard du site."
        setDashboardState({ kind: "error", message })
      }
    }

    void loadDashboard()
    return () => { cancelled = true }
  }, [id])

  useEffect(() => {
    let cancelled = false

    async function loadEmissionFactors() {
      setFactorsState({ kind: "loading" })
      try {
        const response = await api.get<EmissionFactorDTO[]>("/api/emission-factors")
        if (!cancelled) setFactorsState({ kind: "ready", data: response.data })
      } catch (e: unknown) {
        if (cancelled) return
        const axiosError = e as AxiosError<{ message?: string }>
        const message =
          axiosError.response?.data?.message ??
          axiosError.message ??
          "Erreur lors du chargement des facteurs d'émission."
        setFactorsState({ kind: "error", message })
      }
    }

    void loadEmissionFactors()
    return () => { cancelled = true }
  }, [])

  const view = useMemo(() => {
    if (dashboardState.kind !== "ready" || factorsState.kind !== "ready") return null

    const dashboard = dashboardState.data
    const factorsById = new Map<number, EmissionFactorDTO>(
      factorsState.data.map((f) => [f.id, f])
    )

    type MaterialItem = {
      material: SiteMaterialDTO
      factor: EmissionFactorDTO
      co2Kg: number
    }

    const items = dashboard.siteMaterials
      .map((m: SiteMaterialDTO): MaterialItem | null => {
        const factor = factorsById.get(m.emissionFactorId)
        if (!factor) return null
        return { material: m, factor, co2Kg: m.quantityKg * factor.factorKgCo2PerKg }
      })
      .filter((x: MaterialItem | null): x is MaterialItem => x !== null)

    type CategoryAcc = { constructionKg: number; energyKg: number; parkingKg: number }
    const byCategory = items.reduce(
      (acc: CategoryAcc, x: MaterialItem) => {
        if (x.factor.category === "construction") acc.constructionKg += x.co2Kg
        else if (x.factor.category === "energy") acc.energyKg += x.co2Kg
        else if (x.factor.category === "parking") acc.parkingKg += x.co2Kg
        return acc
      },
      { constructionKg: 0, energyKg: 0, parkingKg: 0 }
    )

    const co2TotalKg = byCategory.constructionKg + byCategory.energyKg + byCategory.parkingKg
    const co2PerM2 = safeDiv(co2TotalKg, dashboard.site.surfaceM2)
    const co2PerEmployee = safeDiv(co2TotalKg, dashboard.site.nbEmployees)

    const byMaterial = items.reduce((acc: Map<string, number>, x: MaterialItem) => {
      acc.set(x.factor.materialName, (acc.get(x.factor.materialName) ?? 0) + x.co2Kg)
      return acc
    }, new Map<string, number>())

    type MaterialEntry = { materialName: string; co2Kg: number }
    const materials: MaterialEntry[] = (Array.from(byMaterial.entries()) as [string, number][])
      .map(([materialName, co2Kg]) => ({ materialName, co2Kg }))
      .sort((a, b) => b.co2Kg - a.co2Kg)

    return { dashboard, computed: { byCategory, co2TotalKg, co2PerM2, co2PerEmployee, materials, items } }
  }, [dashboardState, factorsState])

  const pageTitle = useMemo(() => {
    if (dashboardState.kind !== "ready") return `Site ${id}`
    return dashboardState.data.site.name
  }, [dashboardState, id])

  const categoryPieData = useMemo(() => {
    if (!view) return []
    return [
      { key: "construction", label: "Construction", value: view.computed.byCategory.constructionKg },
      { key: "energy", label: "Énergie", value: view.computed.byCategory.energyKg },
      { key: "parking", label: "Parking", value: view.computed.byCategory.parkingKg },
    ]
  }, [view])

  const categoryChartConfig = useMemo((): ChartConfig => {
    return Object.fromEntries(
      categoryPieData.map((d, idx) => [
        d.key,
        { label: d.label, color: `var(--chart-${idx + 1})` },
      ])
    ) as ChartConfig
  }, [categoryPieData])

  const materialsPieData = useMemo(() => {
    if (!view) return []
    return view.computed.materials.map((m: { materialName: string; co2Kg: number }, idx: number) => ({
      name: m.materialName,
      chartKey: toChartKey(m.materialName),
      colorIdx: (idx % 5) + 1,
      value: m.co2Kg,
    }))
  }, [view])

  const materialsChartConfig = useMemo((): ChartConfig => {
    return Object.fromEntries(
      materialsPieData.map((d) => [
        d.chartKey,
        { label: d.name, color: `var(--chart-${d.colorIdx})` },
      ])
    ) as ChartConfig
  }, [materialsPieData])

  const materialsBarData = useMemo(() => {
    if (!view) return []
    return view.computed.materials.map((m: { materialName: string; co2Kg: number }) => ({
      materialName: m.materialName,
      co2Kg: m.co2Kg,
    }))
  }, [view])

  const materialsBarConfig = useMemo((): ChartConfig => ({
    co2Kg: { label: "CO₂ (kg)", color: "var(--chart-4)" },
  }), [])

  const hasError = dashboardState.kind === "error" || factorsState.kind === "error"
  const errorMessage =
    dashboardState.kind === "error"
      ? dashboardState.message
      : factorsState.kind === "error"
        ? factorsState.message
        : null

  const energyKwhYear =
    dashboardState.kind === "ready" ? (dashboardState.data.energyKwhYear ?? null) : undefined

  return (
    <div className="mx-auto flex w-full max-w-6xl flex-col gap-6 px-4 py-6">
      <header className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div className="flex flex-col gap-2">
          <div className="flex items-center gap-2">
            <h1 className="text-2xl font-semibold tracking-tight">{pageTitle}</h1>
          </div>
          <p className="text-sm text-muted-foreground">
            KPIs, graphiques, et résumé des données du site.
          </p>
        </div>
        <Button asChild variant="outline">
          <Link href="/dashboard">Retour au dashboard</Link>
        </Button>
      </header>

      <Separator />

      {hasError && errorMessage && (
        <Alert variant="destructive">
          <AlertTitle>Erreur</AlertTitle>
          <AlertDescription>{errorMessage}</AlertDescription>
        </Alert>
      )}

      <main className="flex flex-col gap-6">
        {/* ── KPI Cards ── */}
        <section className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
          <Card size="sm">
            <CardHeader>
              <CardTitle>CO₂ Total</CardTitle>
              <CardDescription>Total estimé</CardDescription>
            </CardHeader>
            <CardContent>
              {view ? (
                <div className="text-2xl font-semibold">{formatKg(view.computed.co2TotalKg)}</div>
              ) : (
                <Skeleton className="h-8 w-32" />
              )}
            </CardContent>
          </Card>

          <Card size="sm">
            <CardHeader>
              <CardTitle>CO₂ / m²</CardTitle>
              <CardDescription>Intensité surface</CardDescription>
            </CardHeader>
            <CardContent>
              {view ? (
                <div className="text-2xl font-semibold">
                  {formatKgPerUnit(view.computed.co2PerM2)}
                  <span className="text-sm text-muted-foreground"> / m²</span>
                </div>
              ) : (
                <Skeleton className="h-8 w-32" />
              )}
            </CardContent>
          </Card>

          <Card size="sm">
            <CardHeader>
              <CardTitle>CO₂ / Employé</CardTitle>
              <CardDescription>Intensité effectif</CardDescription>
            </CardHeader>
            <CardContent>
              {view ? (
                <div className="text-2xl font-semibold">
                  {formatKgPerUnit(view.computed.co2PerEmployee)}
                  <span className="text-sm text-muted-foreground"> / emp.</span>
                </div>
              ) : (
                <Skeleton className="h-8 w-32" />
              )}
            </CardContent>
          </Card>

          <Card size="sm">
            <CardHeader>
              <CardTitle>Énergie annuelle</CardTitle>
              <CardDescription>Consommation</CardDescription>
            </CardHeader>
            <CardContent>
              {dashboardState.kind === "ready" ? (
                <div className="text-2xl font-semibold">
                  {formatKwhYear(energyKwhYear)}
                </div>
              ) : (
                <Skeleton className="h-8 w-32" />
              )}
            </CardContent>
          </Card>
        </section>

        {/* ── Charts row (2-col) ── */}
        <section className="grid gap-4 lg:grid-cols-2">
          <Card>
            <CardHeader>
              <CardTitle>Construction vs Exploitation</CardTitle>
              <CardDescription>Répartition des émissions CO₂</CardDescription>
            </CardHeader>
            <CardContent>
              {view ? (
                <ChartContainer className="h-64 w-full" config={categoryChartConfig}>
                  <PieChart>
                    <ChartTooltip content={<ChartTooltipContent nameKey="key" />} />
                    <Pie data={categoryPieData} dataKey="value" nameKey="key" outerRadius={90}>
                      {categoryPieData.map((entry, idx) => (
                        <Cell key={entry.key} fill={`var(--chart-${idx + 1})`} />
                      ))}
                    </Pie>
                    <ChartLegend content={<ChartLegendContent nameKey="key" />} />
                  </PieChart>
                </ChartContainer>
              ) : (
                <Skeleton className="h-64 w-full" />
              )}
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>Répartition Matériaux</CardTitle>
              <CardDescription>Parts par matériau (CO₂)</CardDescription>
              <CardAction>
                {view ? (
                  <Badge variant="outline">{view.computed.materials.length} matériaux</Badge>
                ) : (
                  <Skeleton className="h-5 w-24" />
                )}
              </CardAction>
            </CardHeader>
            <CardContent>
              {view ? (
                <ChartContainer className="h-64 w-full" config={materialsChartConfig}>
                  <PieChart>
                    <ChartTooltip
                      content={
                        <ChartTooltipContent
                          hideLabel
                          formatter={(value, _name, item) => {
                            const name = String(item.payload?.name ?? "—")
                            const n = typeof value === "number" ? value : Number(value)
                            return (
                              <div className="flex w-full items-center justify-between gap-6">
                                <span className="text-muted-foreground">{name}</span>
                                <span className="font-mono font-medium tabular-nums">
                                  {formatKg(n)}
                                </span>
                              </div>
                            )
                          }}
                        />
                      }
                    />
                    <Pie data={materialsPieData} dataKey="value" nameKey="chartKey" outerRadius={90}>
                      {materialsPieData.map((entry) => (
                        <Cell
                          key={entry.chartKey}
                          fill={`var(--chart-${entry.colorIdx})`}
                        />
                      ))}
                    </Pie>
                    <ChartLegend content={<ChartLegendContent nameKey="chartKey" />} />
                  </PieChart>
                </ChartContainer>
              ) : (
                <Skeleton className="h-64 w-full" />
              )}
            </CardContent>
          </Card>
        </section>

        {/* ── Bar chart (full width) ── */}
        <section>
          <Card>
            <CardHeader>
              <CardTitle>Détail des émissions par matériau</CardTitle>
              <CardDescription>kgCO₂ calculés à partir des facteurs d&apos;émission</CardDescription>
            </CardHeader>
            <CardContent>
              {view ? (
                <ChartContainer className="h-80 w-full" config={materialsBarConfig}>
                  <BarChart data={materialsBarData} margin={{ left: 8, right: 8 }}>
                    <ChartTooltip content={<ChartTooltipContent nameKey="materialName" />} />
                    <XAxis
                      dataKey="materialName"
                      tickLine={false}
                      axisLine={false}
                      interval={0}
                      angle={-20}
                      textAnchor="end"
                      height={60}
                    />
                    <YAxis
                      tickLine={false}
                      axisLine={false}
                      tickFormatter={(v: number) => nf.format(v)}
                    />
                    <Bar dataKey="co2Kg" fill="var(--color-co2Kg)" radius={6} />
                  </BarChart>
                </ChartContainer>
              ) : (
                <Skeleton className="h-80 w-full" />
              )}
            </CardContent>
          </Card>
        </section>

        {/* ── Emissions table ── */}
        <section>
          <Card>
            <CardHeader>
              <CardTitle>Détail par matériau</CardTitle>
              <CardDescription>Quantités et émissions calculées par ligne</CardDescription>
              {view && (
                <CardAction>
                  <Badge variant="outline">{view.computed.items.length} lignes</Badge>
                </CardAction>
              )}
            </CardHeader>
            <CardContent className="p-0">
              {view ? (
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>Matériau</TableHead>
                      <TableHead>Catégorie</TableHead>
                      <TableHead className="text-right">Quantité (kg)</TableHead>
                      <TableHead className="text-right">Facteur (kgCO₂/kg)</TableHead>
                      <TableHead className="text-right">CO₂ estimé</TableHead>
                      <TableHead className="hidden text-right lg:table-cell">Source</TableHead>
                      <TableHead className="hidden text-right lg:table-cell">Année</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {view.computed.items
                      .slice()
                      .sort((a, b) => b.co2Kg - a.co2Kg)
                      .map((item) => (
                        <TableRow key={item.material.id}>
                          <TableCell className="font-medium">
                            {item.factor.materialName}
                          </TableCell>
                          <TableCell>
                            <Badge variant="secondary" className="capitalize">
                              {item.factor.category}
                            </Badge>
                          </TableCell>
                          <TableCell className="text-right tabular-nums">
                            {nf.format(item.material.quantityKg)}
                          </TableCell>
                          <TableCell className="text-right tabular-nums">
                            {nf2.format(item.factor.factorKgCo2PerKg)}
                          </TableCell>
                          <TableCell className="text-right font-medium tabular-nums">
                            {formatKg(item.co2Kg)}
                          </TableCell>
                          <TableCell className="hidden text-right text-muted-foreground lg:table-cell">
                            {item.factor.source}
                          </TableCell>
                          <TableCell className="hidden text-right tabular-nums lg:table-cell">
                            {item.factor.year}
                          </TableCell>
                        </TableRow>
                      ))}
                  </TableBody>
                </Table>
              ) : (
                <div className="flex flex-col gap-2 p-4">
                  {Array.from({ length: 4 }).map((_, i) => (
                    <Skeleton key={i} className="h-10 w-full" />
                  ))}
                </div>
              )}
            </CardContent>
          </Card>
        </section>

        {/* ── Site summary ── */}
        <section>
          <Card>
            <CardHeader>
              <CardTitle>Résumé du site</CardTitle>
              <CardDescription>Données principales</CardDescription>
            </CardHeader>
            <CardContent>
              {dashboardState.kind === "ready" ? (
                <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
                  {(
                    [
                      { label: "Token", value: <span className="font-mono text-xs">{dashboardState.data.site.token}</span> },
                      { label: "Adresse", value: dashboardState.data.site.address ?? "—" },
                      { label: "Ville", value: dashboardState.data.site.city ?? "—" },
                      {
                        label: "Surface",
                        value: typeof dashboardState.data.site.surfaceM2 === "number"
                          ? `${nf.format(dashboardState.data.site.surfaceM2)} m²`
                          : "—",
                      },
                      { label: "Employés", value: nf.format(dashboardState.data.site.nbEmployees) },
                      { label: "Postes de travail", value: nf.format(dashboardState.data.site.nbWorkstations) },
                      { label: "Parking sous-dalle", value: nf.format(dashboardState.data.site.parkingUnderground) },
                      { label: "Parking sous-sol", value: nf.format(dashboardState.data.site.parkingBasement) },
                      { label: "Parking aérien", value: nf.format(dashboardState.data.site.parkingOutdoor) },
                    ] as { label: string; value: React.ReactNode }[]
                  ).map(({ label, value }) => (
                    <div key={label} className="flex flex-col gap-1">
                      <div className="text-xs text-muted-foreground">{label}</div>
                      <div className="text-sm">{value}</div>
                    </div>
                  ))}
                </div>
              ) : (
                <div className="grid gap-3 sm:grid-cols-2 lg:grid-cols-3">
                  {Array.from({ length: 9 }).map((_, i) => (
                    <Skeleton key={i} className="h-10 w-full" />
                  ))}
                </div>
              )}
            </CardContent>
          </Card>
        </section>
      </main>
    </div>
  )
}
