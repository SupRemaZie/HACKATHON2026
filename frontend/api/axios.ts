import axios from "axios"

export const api = axios.create({
  baseURL: "http://localhost:8080/api",
  headers: {
    "Content-Type": "application/json",
  },
})

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token")
    if (token) {
      config.headers["Authorization"] = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    console.error("Request error:", error)
    return Promise.reject(error)
  }
)

// Response interceptor to handle 401 and 403 response
api.interceptors.response.use(
  (response) => {
    return response
  },
  async (error) => {
    // Check if error response is present and error status is 401 or 403
    if (
      error.response &&
      (error.response.status === 401 || error.response.status === 403)
    ) {
      console.error(
        "Response error :: " + error.response.status + " ==>",
        error.response
      )

      // fetch new access token
      try {
        const response = await api.post("/auth/refresh-token", {
          refreshToken: localStorage.getItem("refreshToken"),
        })

        const newAccessToken = response.data.token
        localStorage.setItem("token", newAccessToken)

        console.log("Access token refreshed successfully");
        // Re-try the original request
        const originalRequest = error.config
        originalRequest.headers.Authorization = `Bearer ${newAccessToken}`
        return await axios(originalRequest)
      } catch (refreshError) {
        localStorage.removeItem("token")
        localStorage.removeItem("refreshToken")
        localStorage.removeItem("role")
        window.location.href = "/auth/login"
        return Promise.reject(refreshError)
      }
    }
    return Promise.reject(error)
  }
)
