"use client";
import Link from 'next/link';
import { loginApiRequest } from './login.service';
import { useState } from 'react';
import { LoginRequestDTO } from '@/types/auth/authDTO';
import { useRouter } from 'next/navigation';

export default function LoginPage() {

  const router = useRouter();
  const [credentials, setCredentials] = useState<LoginRequestDTO>({ email: "admin@hackathon.local", password: "password" });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleLogin = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      const response = await loginApiRequest(credentials);

      console.log("Réponse de l'API :", response.data);
      if (response.data.token) {
        localStorage.setItem("token", response.data.token);
        localStorage.setItem("refreshToken", response.data.refreshToken);
        router.push("/dashboard");
      }
    } catch (error: any) {
      console.error("Erreur lors de la connexion :", error);
      const errorMessage = error?.response?.data?.message || "Identifiants invalides. Veuillez réessayer.";
      setError(errorMessage);
      // alert(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-green-100">
      <div className="bg-white p-8 rounded-lg shadow-md w-96">
        <h2 className="text-2xl font-bold mb-6 text-center text-black">Connexion</h2>
        {error && (
          <div className="mb-4 p-3 bg-red-100 border border-red-400 text-red-700 rounded-md">
            {error}
          </div>
        )}
        <form onSubmit={handleLogin} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-black">Email</label>
            <input 
              type="email" 
              required 
              disabled={loading}
              className="mt-1 w-full p-2 border text-black rounded-md focus:ring-blue-500 focus:border-blue-500 disabled:bg-gray-100" 
              value={credentials.email}
              onChange={(e) => setCredentials({...credentials, email: e.target.value})}
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-black">Mot de passe</label>
            <input 
              type="password" 
              required 
              disabled={loading}
              className="mt-1 w-full p-2 border text-black rounded-md focus:ring-blue-500 focus:border-blue-500 disabled:bg-gray-100" 
              value={credentials.password}
              onChange={(e) => setCredentials({...credentials, password: e.target.value})}
            />
          </div>
          <button 
            type="submit" 
            disabled={loading}
            className="w-full bg-green-600 text-white py-2 rounded-md hover:bg-green-700 transition disabled:bg-gray-400 disabled:cursor-not-allowed"
          >
            {loading ? "Connexion en cours..." : "Se connecter"}
          </button>

        </form>
        <p className="mt-4 text-sm text-center text-black">
          Pas de compte ? <Link href="/auth/register" className="text-green-600 hover:underline">S'inscrire</Link>
        </p>
      </div>
    </div>
  );
}