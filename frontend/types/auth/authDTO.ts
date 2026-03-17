
export interface LoginRequestDTO {
    email: string;
    password: string;
}

export interface LoginResponseDTO {
    token: string;
    tokenType: string;
    expiresIn: number;
    email: string;
    refreshToken: string;
    refreshExpiresIn: number;
}