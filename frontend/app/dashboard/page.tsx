"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { useEffect, useMemo, useState } from "react";
import type { AxiosError } from "axios";

import { api } from "@/api/axios";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import {
  Empty,
  EmptyContent,
  EmptyDescription,
  EmptyHeader,
  EmptyTitle,
} from "@/components/ui/empty";
import { Separator } from "@/components/ui/separator";
import { Spinner } from "@/components/ui/spinner";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";

type SiteResponseDTO = {
  id: number;
  token: string;
  name: string;
  address: string | null;
  city: string | null;
  surfaceM2: number | null;
  nbEmployees: number;
  nbWorkstations: number;
  parkingUnderground: number;
  parkingBasement: number;
  parkingOutdoor: number;
  createdAt: string;
  updatedAt: string;
};

type SitesState =
  | { kind: "loading" }
  | { kind: "error"; message: string }
  | { kind: "ready"; sites: SiteResponseDTO[] };

export default function DashboardPage() {
  const router = useRouter();
  const [sitesState, setSitesState] = useState<SitesState>({ kind: "loading" });

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("refreshToken");
    router.push("/auth/login");
  };

  useEffect(() => {
    let cancelled = false;

    async function loadSites() {
      setSitesState({ kind: "loading" });
      try {
        const response = await api.get<SiteResponseDTO[]>("/api/sites");
        if (cancelled) return;
        setSitesState({ kind: "ready", sites: response.data });
      } catch (e: unknown) {
        if (cancelled) return;

        const axiosError = e as AxiosError<{ message?: string }>;
        const message =
          axiosError.response?.data?.message ??
          axiosError.message ??
          "Erreur lors du chargement des sites.";

        setSitesState({ kind: "error", message });
      }
    }

    void loadSites();

    return () => {
      cancelled = true;
    };
  }, []);

  const sitesCount = useMemo(() => {
    if (sitesState.kind !== "ready") return 0;
    return sitesState.sites.length;
  }, [sitesState]);

  return (
    <div className="mx-auto flex w-full max-w-6xl flex-col gap-6 px-4 py-6">
      <header className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div className="flex flex-col gap-1">
          <h1 className="text-2xl font-semibold tracking-tight">Dashboard</h1>
          <p className="text-sm text-muted-foreground">
            Gérez vos sites et créez-en de nouveaux.
          </p>
        </div>

        <div className="flex items-center gap-2">
          <Button asChild>
            <Link href="/create">Créer un site</Link>
          </Button>
          <Button variant="outline" onClick={handleLogout}>
            Logout
          </Button>
        </div>
      </header>

      <Separator />

      <main className="flex flex-col gap-4">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between gap-4">
            <CardTitle className="text-base">Mes sites</CardTitle>
            <div className="text-sm text-muted-foreground">
              {sitesState.kind === "ready" ? `${sitesCount} site(s)` : "—"}
            </div>
          </CardHeader>

          <CardContent>
            {sitesState.kind === "loading" && (
              <div className="flex items-center gap-2 text-sm text-muted-foreground">
                <Spinner />
                Chargement des sites…
              </div>
            )}

            {sitesState.kind === "error" && (
              <div className="text-sm text-destructive">{sitesState.message}</div>
            )}

            {sitesState.kind === "ready" && sitesState.sites.length === 0 && (
              <Empty>
                <EmptyHeader>
                  <EmptyTitle>Aucun site</EmptyTitle>
                  <EmptyDescription>
                    Créez votre premier site pour commencer.
                  </EmptyDescription>
                </EmptyHeader>
                <EmptyContent>
                  <Button asChild>
                    <Link href="/create">Créer un site</Link>
                  </Button>
                </EmptyContent>
              </Empty>
            )}

            {sitesState.kind === "ready" && sitesState.sites.length > 0 && (
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Nom</TableHead>
                    <TableHead>Ville</TableHead>
                    <TableHead className="text-right">Surface (m²)</TableHead>
                    <TableHead className="text-right">Employés</TableHead>
                    <TableHead>Token</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {sitesState.sites.map((s) => (
                    <TableRow
                      key={s.id}
                      role="link"
                      tabIndex={0}
                      className="cursor-pointer"
                      onClick={() => router.push(`/dashboard/${s.token}`)}
                      onKeyDown={(e) => {
                        if (e.key === "Enter" || e.key === " ") {
                          e.preventDefault();
                          router.push(`/dashboard/${s.token}`);
                        }
                      }}
                    >
                      <TableCell className="font-medium">{s.name}</TableCell>
                      <TableCell>{s.city ?? "—"}</TableCell>
                      <TableCell className="text-right">
                        {typeof s.surfaceM2 === "number" ? s.surfaceM2 : "—"}
                      </TableCell>
                      <TableCell className="text-right">
                        {s.nbEmployees}
                      </TableCell>
                      <TableCell className="font-mono text-xs">
                        {s.token}
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            )}
          </CardContent>
        </Card>
      </main>
    </div>
  );
}