"use client";
import Link from 'next/link';

export default function LoginPage() {
  const handleLogin = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    console.log("Tentative de connexion : Données envoyées au backend...");
    alert("Vérifiez la console !");
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-green-100">
      <div className="bg-white p-8 rounded-lg shadow-md w-96">
        <h2 className="text-2xl font-bold mb-6 text-center text-gray-800">Connexion</h2>
        <form onSubmit={handleLogin} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700">Email</label>
            <input type="email" required className="mt-1 w-full p-2 border rounded-md focus:ring-blue-500 focus:border-blue-500" />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">Mot de passe</label>
            <input type="password" required className="mt-1 w-full p-2 border rounded-md" />
          </div>
          <button type="submit" className="w-full bg-green-600 text-white py-2 rounded-md hover:bg-green-700 transition">
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