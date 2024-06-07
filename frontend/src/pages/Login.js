import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api";

const Login = () => {
	const [username, setUsername] = useState("");
	const [password, setPassword] = useState("");
	const navigate = useNavigate();

	const handleLogin = async (e) => {
		e.preventDefault();
		try {
			const response = await api.post("/login", {
				username: username,
				password: password,
			});

			// console.log(response);
			localStorage.setItem("accessToken", response.data.access_token);
			localStorage.setItem("refreshToken", response.data.refresh_token);
			// console.log(localStorage.getItem("accessToken"));
			// console.log(localStorage.getItem("refreshToken"));
			navigate("/dashboard");
		} catch (error) {
			console.error(error);
		}
	};

	return (
		<div className="flex items-center justify-center min-h-screen bg-gray-100">
			<form
				onSubmit={handleLogin}
				className="bg-white p-6 rounded shadow-md w-full max-w-sm"
			>
				<h2 className="text-2xl mb-4">Login</h2>
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
					Login
				</button>
			</form>
		</div>
	);
};
export default Login;
