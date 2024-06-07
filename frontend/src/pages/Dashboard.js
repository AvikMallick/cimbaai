import { useEffect, useState } from "react";
// import { useNavigate } from "react-router-dom";
import api from "../api";

const Dashboard = () => {
	const [url, setUrl] = useState("");
	const [requests, setRequests] = useState([]);
	// const navigate = useNavigate();

	// Fetch the user's request history when the component mounts
	useEffect(() => {
		const fetchRequests = async () => {
			try {
				const response = await api.get("/get-request-history");
				setRequests(response);
			} catch (error) {
				console.error(error);
			}
		};
		fetchRequests();
	}, []);

	// Handle the submission of a new URL
	const handleRequest = async (e) => {
		e.preventDefault();
		try {
			console.log("url: ", url);
			const response = await api.post("/get-summary", { url });
			console.log(response.data);
			setRequests([...requests, response.data]);
			setUrl("");
		} catch (error) {
			console.error(error);
		}
	};

	return (
		<div className="min-h-screen p-6 bg-gray-100">
			<form
				onSubmit={handleRequest}
				className="bg-white p-6 rounded shadow-md max-w-md mx-auto mb-6"
			>
				<h2 className="text-2xl mb-4">Submit URL</h2>
				<div className="mb-4">
					<label className="block mb-1">URL</label>
					<input
						type="url"
						className="w-full p-2 border rounded"
						value={url}
						onChange={(e) => setUrl(e.target.value)}
						required
					/>
				</div>
				<button
					type="submit"
					className="w-full bg-blue-500 text-white p-2 rounded"
				>
					Submit
				</button>
			</form>
			<div className="bg-white p-6 rounded shadow-md max-w-md mx-auto">
				<h2 className="text-2xl mb-4">Request History</h2>
				<ul>
					{/* {requests?.map((request, index) => (
						<li key={index} className="mb-2">
							<div className="p-2 border rounded">{request.url}</div>
						</li>
					))} */}
					<div>Demo for now</div>
				</ul>
			</div>
		</div>
	);
};
export default Dashboard;
