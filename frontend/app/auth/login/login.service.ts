import { api } from "@/api/axios";
import { LoginRequestDTO, LoginResponseDTO } from "@/types/auth/authDTO";

export const testApiRequest = () => {
  return api.get("/api/test");
};

export const loginApiRequest = (credentials: LoginRequestDTO) => {
  return api.post<LoginResponseDTO>("/api/auth/login", credentials);
};
