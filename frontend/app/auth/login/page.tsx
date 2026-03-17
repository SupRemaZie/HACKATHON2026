"use client";

import Link from "next/link";
import { loginApiRequest } from "./login.service";
import { useState } from "react";
import { LoginRequestDTO } from "@/types/auth/authDTO";
import { useRouter } from "next/navigation";
import { AxiosError } from "axios";
import {
  Card,
  CardHeader,
  CardTitle,
  CardDescription,
  CardContent,
  CardFooter,
} from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert";
import { Field, FieldGroup, FieldLabel } from "@/components/ui/field";

function hasMessage(value: unknown): value is { message: string } {
  if (!value || typeof value !== "object") return false;
  const record = value as Record<string, unknown>;
  return typeof record.message === "string";
}

function getLoginErrorMessage(error: unknown): string {
  const fallback = "Identifiants invalides. Veuillez réessayer.";

  if (error instanceof AxiosError) {
    const data: unknown = error.response?.data;
    if (hasMessage(data)) return data.message;
    if (typeof error.message === "string" && error.message.trim().length > 0) {
      return error.message;
    }
    return fallback;
  }

  if (error instanceof Error && error.message.trim().length > 0) {
    return error.message;
  }

  return fallback;
}

export default function LoginPage() {
  const router = useRouter();
  const [credentials, setCredentials] = useState<LoginRequestDTO>({
    email: "admin@carbontrack.local",
    password: "password",
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleLogin = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      const response = await loginApiRequest(credentials);

      if (response.data.token) {
        localStorage.setItem("token", response.data.token);
        localStorage.setItem("refreshToken", response.data.refreshToken);
        router.push("/dashboard");
      }
    } catch (err: unknown) {
      setError(getLoginErrorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-background p-4">
      <div className="mx-auto grid min-h-[calc(100vh-2rem)] max-w-sm place-items-center">
      <Card className="w-full max-w-sm">
        <CardHeader>
          <CardTitle>Connexion</CardTitle>
          <CardDescription>
            Connectez-vous à votre compte CarbonTrack
          </CardDescription>
        </CardHeader>
        <CardContent className="flex flex-col gap-4">
          {error && (
            <Alert variant="destructive">
              <AlertTitle>Connexion impossible</AlertTitle>
              <AlertDescription>{error}</AlertDescription>
            </Alert>
          )}
          <form onSubmit={handleLogin} className="flex flex-col gap-4">
            <FieldGroup>
              <Field data-disabled={loading}>
              <FieldLabel htmlFor="email">Email</FieldLabel>
              <Input
                id="email"
                type="email"
                required
                disabled={loading}
                placeholder="admin@carbontrack.local"
                value={credentials.email}
                onChange={(e) =>
                  setCredentials({ ...credentials, email: e.target.value })
                }
              />
              </Field>
              <Field data-disabled={loading}>
              <FieldLabel htmlFor="password">Mot de passe</FieldLabel>
              <Input
                id="password"
                type="password"
                required
                disabled={loading}
                placeholder="••••••••"
                value={credentials.password}
                onChange={(e) =>
                  setCredentials({ ...credentials, password: e.target.value })
                }
              />
              </Field>
            </FieldGroup>
            <Button type="submit" disabled={loading} className="w-full">
              {loading ? "Connexion en cours..." : "Se connecter"}
            </Button>
          </form>
        </CardContent>
        <CardFooter className="flex justify-center">
          <p className="text-sm text-muted-foreground">
            Pas de compte ?{" "}
            <Link
              href="/auth/register"
              className="font-medium text-primary hover:underline"
            >
              S&apos;inscrire
            </Link>
          </p>
        </CardFooter>
      </Card>
      </div>
    </div>
  );
}
