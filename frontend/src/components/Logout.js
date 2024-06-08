import { useNavigate } from "react-router-dom";

const Logout = () => {
	const navigate = useNavigate();

	const handleLogout = () => {
		// Delete tokens from local storage
		localStorage.removeItem("accessToken");
		localStorage.removeItem("refreshToken");

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
