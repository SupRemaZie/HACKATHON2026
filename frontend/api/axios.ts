import axios from "axios"

export const api = axios.create({
  baseURL: "http://localhost:8080/api",
  headers: {
    "Content-Type": "application/json",
  },
})

const getBackendErrorMessage = (error: any): string => {
  const data = error?.response?.data

  if (typeof data === "string") {
    return data
  }

  if (data?.message) {
    return data.message
  }

  if (data?.error) {
    return data.error
  }

  return error?.message || "Unknown error"
}

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token")
    if (token) {
      config.headers = config.headers ?? {}
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    console.error("Request error:", getBackendErrorMessage(error))
    return Promise.reject(error)
  }
)

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const requestUrl: string = error.config?.url ?? ""
    const isAuthEndpoint =
      requestUrl.includes("/auth/login") ||
      requestUrl.includes("/auth/register") ||
      requestUrl.includes("/auth/refresh-token")

    const backendMessage = getBackendErrorMessage(error)
    console.error("API error:", backendMessage)

    if (
      error.response &&
      (error.response.status === 401 || error.response.status === 403) &&
      !isAuthEndpoint
    ) {
      try {
        const refreshToken = localStorage.getItem("refreshToken")

        if (!refreshToken) {
          window.location.href = "/auth/login"
          return Promise.reject(error)
        }

        const response = await axios.post(
          "http://localhost:8080/api/auth/refresh-token",
          { refreshToken }
        )

        const newToken = response.data.token

        localStorage.setItem("token", newToken)

        const originalRequest = error.config
        originalRequest.headers = originalRequest.headers ?? {}
        originalRequest.headers.Authorization = `Bearer ${newToken}`

        return axios(originalRequest)
      } catch (refreshError) {
        console.error("Refresh token error:", getBackendErrorMessage(refreshError))
        localStorage.removeItem("token")
        localStorage.removeItem("refreshToken")
        window.location.href = "/auth/login"
        return Promise.reject(refreshError)
      }
    }

    return Promise.reject(error)
  }
)