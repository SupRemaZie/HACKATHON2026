import { api } from "@/api/axios";
import { LoginRequestDTO, LoginResponseDTO } from "@/types/auth/authDTO";

export const testApiRequest = () => {
  return api.get("/test");
};

export const loginApiRequest = (credentials: LoginRequestDTO) => {
  return api.post<LoginResponseDTO>("/auth/login", credentials);
};
