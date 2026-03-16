"use client";
import { useState } from 'react';

export default function CalculateurPage() {
  // État pour les données du site
  const [siteData, setSiteData] = useState({
    name: '',
    surface: 0,
    parking: 0,
    energyConsum: 0,
    employees: 0,
    materials: { beton: 0, acier: 0, verre: 0, bois: 0 }
  });

  // État pour les facteurs d'émission (modifiable par l'utilisateur)
  const [factors, setFactors] = useState({
    beton: 0.12,
    acier: 0.60,
    verre: 1.43,
    bois: 0.03,
    electricite: 0.06 // kgCO2e/kWh
  });

  const [result, setResult] = useState<string | null>(null);


const handleCalculate = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    
    // Calcul simple : (Consom * Facteur Energie) + Somme(Matériau * Facteur Matériau)
    const carbonEnergy = siteData.energyConsum * factors.electricite;
    const carbonMaterials = 
        (siteData.materials.beton * factors.beton) +
        (siteData.materials.acier * factors.acier) +
        (siteData.materials.verre * factors.verre) +
        (siteData.materials.bois * factors.bois);

    setResult((carbonEnergy + carbonMaterials).toFixed(2));
};

  return (
    <div className="max-w-4xl mx-auto p-8">
      <h1 className="text-3xl font-bold mb-6 text-green-700">Calculateur d'Empreinte Carbone Site</h1>
      
      <form onSubmit={handleCalculate} className="grid grid-cols-1 md:grid-cols-2 gap-8">
        
        {/* SECTION 1 : INFOS SITE */}
        <div className="space-y-4 p-4 border rounded-lg bg-gray-50 text-black">
          <h2 className="font-semibold border-b pb-2">Informations du Site</h2>
          <div>
            <label className="block text-sm">Nom du site</label>
            <input type="text" className="w-full p-2 border rounded" onChange={(e) => setSiteData({...siteData, name: e.target.value})} />
          </div>
          <div className="grid grid-cols-2 gap-2">
            <div>
              <label className="block text-sm">Surface (m²)</label>
              <input type="number" className="w-full p-2 border rounded" onChange={(e) => setSiteData({...siteData, surface: parseFloat(e.target.value) || 0})} />
            </div>
            <div>
              <label className="block text-sm">Parking (places)</label>
              <input type="number" className="w-full p-2 border rounded" onChange={(e) => setSiteData({...siteData, parking: parseFloat(e.target.value) || 0})} />
            </div>
          </div>
          <div>
            <label className="block text-sm">Consommation Énergie (kWh/an)</label>
            <input type="number" className="w-full p-2 border rounded" onChange={(e) => setSiteData({...siteData, energyConsum: parseFloat(e.target.value) || 0})} />
          </div>
        </div>

        {/* SECTION 2 : MATÉRIAUX & QUANTITÉS */}
        <div className="space-y-4 p-4 border rounded-lg bg-gray-50 text-black">
          <h2 className="font-semibold border-b pb-2">Matériaux Utilisés (kg)</h2>
          {Object.keys(siteData.materials).map((mat) => (
            <div key={mat} className="flex items-center justify-between">
              <label className="capitalize text-sm">{mat}</label>
              <input 
                type="number" 
                className="w-32 p-2 border rounded" 
                onChange={(e) => setSiteData({
                  ...siteData, 
                  materials: {...siteData.materials, [mat]: parseFloat(e.target.value) || 0}
                })} 
              />
            </div>
          ))}
        </div>

        <button type="submit" className="md:col-span-2 bg-green-600 text-white py-3 rounded-lg font-bold hover:bg-green-700 transition">
          Calculer l'impact CO₂
        </button>
      </form>

      {/* AFFICHAGE DU RÉSULTAT */}
      {result && (
        <div className="mt-8 p-6 bg-green-100 border-2 border-green-500 rounded-xl text-center">
          <h3 className="text-xl text-black">Résultat Estimé pour {siteData.name}</h3>
          <p className="text-4xl font-black text-green-900 mt-2">{result} kgCO₂e</p>
        </div>
      )}
    </div>
  );
}