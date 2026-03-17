"use client"

import Link from "next/link"

import { mockSites } from "@/lib/carbon/mock-sites"
import { Button } from "@/components/ui/button"
import {
  NavigationMenu,
  NavigationMenuItem,
  NavigationMenuList,
  navigationMenuTriggerStyle,
} from "@/components/ui/navigation-menu"
import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"
import { cn } from "@/lib/utils"
import React from "react"

export default function Page() {
  const [status, setStatus] = React.useState<string>("")

  const checkBackend = async () => {
    try {
      const res = await fetch("/api/health")
      setStatus(res.ok ? "Backend OK" : `Erreur ${res.status}`)
    } catch {
      setStatus("Backend injoignable")
    }
  }

  return (
    <div className="mx-auto flex min-h-svh max-w-5xl flex-col gap-4 p-6">
      <header className="sticky top-0 z-20 -mx-6 border-b bg-background/80 px-6 py-4 backdrop-blur">
        <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
          <div className="flex items-center gap-3">
            <div className="size-10 rounded-xl bg-primary/10 ring-1 ring-foreground/10" />
            <div className="leading-tight">
              <h1 className="text-base font-semibold tracking-tight">
                CarbonTrack
              </h1>
            </div>
          </div>

          <div className="flex flex-col gap-3 sm:flex-row sm:items-end sm:justify-end">
            <Select defaultValue="rennes">
              <SelectTrigger id="site-select" className="w-44">
                <SelectValue placeholder="Sélectionner un site" />
              </SelectTrigger>
              <SelectContent>
                <SelectGroup>
                  {mockSites.map((s) => (
                    <SelectItem key={s.id} value={s.id}>
                      {s.name}
                    </SelectItem>
                  ))}
                </SelectGroup>
              </SelectContent>
            </Select>

            <div className="flex items-center justify-between gap-2 sm:justify-end">
              <NavigationMenu
                viewport={false}
                className="justify-start sm:justify-center"
              >
                <NavigationMenuList className="gap-1">
                  <NavigationMenuItem>
                    <Button
                      size="sm"
                      className={cn(navigationMenuTriggerStyle())}
                    >
                      Dashboard
                    </Button>
                  </NavigationMenuItem>
                  <NavigationMenuItem>
                    <Button
                      size="sm"
                      className={cn(navigationMenuTriggerStyle())}
                    >
                      History
                    </Button>
                  </NavigationMenuItem>
                </NavigationMenuList>
              </NavigationMenu>

              <Button asChild>
                <Link href="/create">Créer un site</Link>
              </Button>
            </div>
          </div>
        </div>
      </header>

      <main className="flex flex-1 flex-col gap-4">
        <p>Calculer l&apos;empreinte carbone d&apos;un site physique</p>
        <Button onClick={checkBackend}>Vérifier la connexion au backend</Button>
        {status && <p className="text-sm text-muted-foreground">{status}</p>}
      </main>
    </div>
  )
}
