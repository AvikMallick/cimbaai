import { useState, useEffect } from "react";
import api from "../api";

const useAuth = () => {
	const [isAuthenticated, setIsAuthenticated] = useState(null);

	useEffect(() => {
		const checkAuth = async () => {
			try {
				const response = await api.get("/validate_token", {
					headers: {
						"Authorization": `Bearer ${localStorage.getItem("accessToken")}`,
					},
				});
				console.log(response);
				setIsAuthenticated(response.data.valid);
			} catch (error) {
				setIsAuthenticated(false);
			}
		};
		checkAuth();
	}, []);

	return isAuthenticated;
};

export default useAuth;
