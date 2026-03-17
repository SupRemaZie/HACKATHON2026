"use client";
import Link from 'next/link';
import { loginApiRequest, testApiRequest } from './login.service';
import { useState } from 'react';
import { LoginRequestDTO } from '@/types/auth/authDTO';
import { useRouter } from 'next/navigation';

export default function LoginPage() {

  const router = useRouter();

  const handleLogin = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    console.log("Données envoyées au backend");
  };

  const [credentials, setCredentials] = useState<LoginRequestDTO>({ email: "admin@carbontrack.local", password: "admin123" });

  const testLogin = async () => {
    try {
      const response = await loginApiRequest(credentials);
          // const response = await testApiRequest();

      console.log("Réponse de l'API test :", response.data);
      if (response.data.token) {
        localStorage.setItem("token", response.data.token);
        localStorage.setItem("refreshToken", response.data.refreshToken);
        localStorage.setItem("role", response.data.role ?? "USER");
        // Cookie lisible par le middleware Next.js pour la protection des routes
        document.cookie = `token=${response.data.token}; path=/; max-age=3600; SameSite=Strict`;
        document.cookie = `role=${response.data.role ?? "USER"}; path=/; max-age=3600; SameSite=Strict`;

        router.push("/dashboard");
      }

    } catch (error) {
      console.error("Erreur lors de l'appel à l'API test :", error);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-green-100">
      <div className="bg-white p-8 rounded-lg shadow-md w-96">
        <h2 className="text-2xl font-bold mb-6 text-center text-black">Connexion</h2>
        <form onSubmit={handleLogin} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-black">Email</label>
            <input 
              type="email" 
              required 
              className="mt-1 w-full p-2 border text-black rounded-md focus:ring-blue-500 focus:border-blue-500" 
              value={credentials.email}
              onChange={(e) => setCredentials({...credentials, email: e.target.value})}
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-black">Mot de passe</label>
            <input 
              type="password" 
              required 
              className="mt-1 w-full p-2 border text-black rounded-md focus:ring-blue-500 focus:border-blue-500" 
              value={credentials.password}
              onChange={(e) => setCredentials({...credentials, password: e.target.value})}
            />
          </div>
          <button type="submit" onClick={testLogin} className="w-full bg-green-600 text-white py-2 rounded-md hover:bg-green-700 transition">
            Se connecter
          </button>

        </form>
        <p className="mt-4 text-sm text-center text-black">
          Pas de compte ? <Link href="/auth/register" className="text-green-600 hover:underline">S'inscrire</Link>
        </p>
      </div>
    </div>
  );
}