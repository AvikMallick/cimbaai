// src/api.js
import axios from "axios";
// import { useNavigate } from "react-router-dom";

const api = axios.create({
	// baseURL: "http://localhost:8080",
	baseURL: "http://springboot:8080",
});

api.interceptors.request.use(
	(config) => {
		const token = localStorage.getItem("accessToken");
		if (token) {
			config.headers["Authorization"] = `Bearer ${token}`;
		}
		return config;
	},
	(error) => Promise.reject(error)
);

api.interceptors.response.use(
	(response) => response,
	async (error) => {
		const originalRequest = error.config;
		if (error.response.status === 401 && !originalRequest._retry) {
			originalRequest._retry = true;
			try {
				const refreshToken = localStorage.getItem("refreshToken");
				const response = await axios.post("/refresh_token", {
					refreshToken,
				});
				localStorage.setItem("accessToken", response.data.accessToken);
				api.defaults.headers[
					"Authorization"
				] = `Bearer ${response.data.accessToken}`;
				return api(originalRequest);
			} catch (error) {
				localStorage.removeItem("accessToken");
				localStorage.removeItem("refreshToken");
				window.location.href = "/login"; // Redirect to login page
			}
		}
		return Promise.reject(error);
	}
);

export default api;
