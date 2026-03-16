"use client";
import Link from 'next/link';

export default function RegisterPage() {
  const handleRegister = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    console.log("Création de compte : Enregistrement de l'utilisateur...");
    alert("Action enregistrée en console !");
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-green-100">
      <div className="bg-white p-8 rounded-lg shadow-md w-96">
        <h2 className="text-2xl font-bold mb-6 text-center text-gray-800">Inscription</h2>
        <form onSubmit={handleRegister} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700">Nom complet</label>
            <input type="text" required className="mt-1 w-full p-2 border rounded-md" />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">Email</label>
            <input type="email" required className="mt-1 w-full p-2 border rounded-md" />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">Mot de passe</label>
            <input type="password" required className="mt-1 w-full p-2 border rounded-md" />
          </div>
          <button type="submit" className="w-full bg-green-600 text-white py-2 rounded-md hover:bg-green-700 transition">
            Créer mon compte
          </button>
        </form>
        <p className="mt-4 text-sm text-center text-black">
          Déjà inscrit ? <Link href="/auth/login" className="text-green-600 hover:underline">Se connecter</Link>
        </p>
      </div>
    </div>
  );
}