import { useNavigate } from "react-router-dom";
import api from "../api";

const Logout = () => {
	const navigate = useNavigate();

	const handleLogout = async () => {
		// Delete tokens from local storage
		await api.post("/logout");

		// Navigate to login page after logout
		navigate("/login");
	};

	return (
		<div className="absolute top-0 right-0 mt-4 mr-6">
			<button
				onClick={handleLogout}
				className="px-2 py-1 text-md bg-red-500 text-white rounded hover:bg-red-700"
			>
				Logout
			</button>
		</div>
	);
};

export default Logout;
