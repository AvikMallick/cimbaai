import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api";

const Signup = () => {
	const [username, setUsername] = useState("");
	const [password, setPassword] = useState("");
	const navigate = useNavigate();

	useEffect(() => {
		const logout = async () => {
			await api.post("/logout");
		};
		logout();
	}, []);

	const handleSignup = async (e) => {
		e.preventDefault();
		try {
			const response = await api.post("/register", { username, password });
			localStorage.setItem("accessToken", response.data.access_token);
			localStorage.setItem("refreshToken", response.data.refresh_token);
			navigate("/dashboard");
		} catch (error) {
			console.error(error);
		}
	};

	const goToLogin = () => {
		navigate("/login");
	};

	return (
		<div className="flex items-center justify-center min-h-screen bg-gray-100">
			<form
				onSubmit={handleSignup}
				className="bg-white p-6 rounded shadow-md w-full max-w-sm"
			>
				<h2 className="text-2xl mb-4">Signup</h2>
				<div className="mb-4">
					<label className="block mb-1">Username</label>
					<input
						type="text"
						className="w-full p-2 border rounded"
						value={username}
						onChange={(e) => setUsername(e.target.value)}
					/>
				</div>
				<div className="mb-4">
					<label className="block mb-1">Password</label>
					<input
						type="password"
						className="w-full p-2 border rounded"
						value={password}
						onChange={(e) => setPassword(e.target.value)}
					/>
				</div>
				<button
					type="submit"
					className="w-full bg-blue-500 text-white p-2 rounded"
				>
					Signup
				</button>

				<button
					onClick={goToLogin}
					className="mt-4 w-full bg-green-500 text-white p-2 rounded"
				>
					Go to Login page
				</button>
			</form>
		</div>
	);
};
export default Signup;
