import { Navigate } from "react-router-dom";
import useAuth from "../hooks/useAuth";

const PrivateRoute = ({ children }) => {
	const isAuthenticated = useAuth();

	if (isAuthenticated === null) {
		return <div>Loading...</div>; // Show a loading indicator while checking authentication
	}

	return isAuthenticated ? children : <Navigate to="/login" />;
};

export default PrivateRoute;
