import { NextResponse } from "next/server"
import type { NextRequest } from "next/server"

const PUBLIC_PATHS = ["/auth/login", "/auth/register", "/"]

// Routes accessibles uniquement aux ADMIN
const ADMIN_PATHS = ["/admin"]

export function middleware(request: NextRequest) {
  const { pathname } = request.nextUrl
  const token = request.cookies.get("token")?.value
  const role = request.cookies.get("role")?.value

  const isPublic = PUBLIC_PATHS.some((p) => pathname === p || pathname.startsWith(p + "/"))

  // Pas de token → redirection vers login (sauf pages publiques)
  if (!isPublic && !token) {
    return NextResponse.redirect(new URL("/auth/login", request.url))
  }

  // Page admin → rôle ADMIN requis
  const isAdminPath = ADMIN_PATHS.some((p) => pathname === p || pathname.startsWith(p + "/"))
  if (isAdminPath && role !== "ADMIN") {
    return NextResponse.redirect(new URL("/dashboard", request.url))
  }

  return NextResponse.next()
}

export const config = {
  matcher: ["/((?!_next/static|_next/image|favicon.ico|.*\\..*).*)"],
}
