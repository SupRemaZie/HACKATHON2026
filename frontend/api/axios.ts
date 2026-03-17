import axios from "axios";

export const api = axios.create({
  baseURL: "http://localhost:8080/api",
  headers: {
    "Content-Type": "application/json"
  }
});

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");
    if (token) {
      config.headers["Authorization"] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    console.error("Request error:", error);
    return Promise.reject(error);
  }
);


// Response interceptor to handle 401 and 403 response
api.interceptors.response.use(
  (response) => {
    return response;
  },
  async (error) => {

// Check if error response is present and error status is 401 or 403
    // Skip refresh logic for auth endpoints to avoid redirect loops on login failures
    const requestUrl: string = error.config?.url ?? "";
    const isAuthEndpoint =
      requestUrl.includes("/auth/login") ||
      requestUrl.includes("/auth/register") ||
      requestUrl.includes("/auth/refresh-token");

    if (
      error.response &&
      (error.response.status === 401 || error.response.status === 403) &&
      !isAuthEndpoint
    ) {
      console.error("Response error :: "+ error.response.status +" ==>", error.response);

      // fetch new access token
      try {
        const refresh_token_url = "/auth/refresh-token/"; 
        const response = await api.post(refresh_token_url, {
          refresh: localStorage.getItem("refresh"), // Get refresh token from local storage
        });

        const newAccesToken = response.data.access;

        localStorage.setItem("access", newAccesToken); // Update the access token in local storage

        console.log("Access token refreshed successfully");
        // Re-try the original request
        const originalRequest = error.config;
        originalRequest.headers.Authorization = `Bearer ${newAccesToken}`;
        return await axios(originalRequest);

      } catch (refreshError) {
        // incase of failed refresh, re-direct to login page
        window.location.href = "/auth/login"; 

// or window.location.href = "/login" if you do not use react-router-dom

        return await Promise.reject(refreshError);
      }
    }
    return Promise.reject(error);
  }
);