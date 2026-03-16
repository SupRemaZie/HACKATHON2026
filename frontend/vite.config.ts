import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
  plugins: [react()],
  server: {
    // Proxy des appels API vers le backend Spring Boot
    proxy: {
      "/api": {
        target: "http://backend:8080",
        changeOrigin: true,
      },
    },
  },
});
