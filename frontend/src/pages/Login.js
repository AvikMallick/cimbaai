import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api";
import config from "../config";

const Login = () => {
	const [username, setUsername] = useState("");
	const [password, setPassword] = useState("");
	const [loginError, setLoginError] = useState(false); // Step 1
	const navigate = useNavigate();

	useEffect(() => {
		const logout = async () => {
			await api.post("/logout");
		};
		logout();
	}, []);

	const handleLogin = async (e) => {
		e.preventDefault();
		console.log("baseUrl: ", config.apiBaseUrl);

		try {
			const response = await api.post("/login", {
				username: username,
				password: password,
			});
			setLoginError(false);

			localStorage.setItem("accessToken", response.data.access_token);
			localStorage.setItem("refreshToken", response.data.refresh_token);

			navigate("/dashboard");
		} catch (error) {
			if (error?.response?.status === 404) {
				setLoginError(true);
			}
			console.error(error);
		}
	};

	const goToSignup = () => {
		navigate("/signup");
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
				{/* Conditional Error Message */}
				<div className={`${loginError ? "block" : "hidden"} text-red-500`}>
					{" "}
					{/* Step 3 */}
					Wrong credentials, please try again.
				</div>

				<button
					onClick={goToSignup}
					className="mt-4 w-full bg-green-500 text-white p-2 rounded"
				>
					Go to Signup page
				</button>
			</form>
		</div>
	);
};
export default Login;
