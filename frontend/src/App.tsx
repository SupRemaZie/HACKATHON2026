import { useState } from "react";

function App() {
  const [status, setStatus] = useState<string>("");

  const checkBackend = async () => {
    try {
      const res = await fetch("/api/health");
      setStatus(res.ok ? "Backend OK" : `Erreur ${res.status}`);
    } catch {
      setStatus("Backend injoignable");
    }
  };

  return (
    <div style={{ maxWidth: 600, margin: "4rem auto", fontFamily: "sans-serif", textAlign: "center" }}>
      <h1>Carbon Footprint</h1>
      <p>Calculer l'empreinte carbone d'un site physique</p>
      <button onClick={checkBackend} style={{ padding: "0.5rem 1.5rem", fontSize: "1rem", cursor: "pointer" }}>
        Tester la connexion au backend
      </button>
      {status && <p style={{ marginTop: "1rem" }}>{status}</p>}
    </div>
  );
}

export default App;
