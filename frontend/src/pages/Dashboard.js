import { useEffect, useState } from "react";
// import { useNavigate } from "react-router-dom";
import api from "../api";
import Logout from "../components/Logout";
import { SummaryResponse } from "../models/responseModels";
import SummaryHistoryItem from "../components/SummaryHistoryItem";
import { extractTime } from "../utils/TimeExtractor";

const Dashboard = () => {
	const [url, setUrl] = useState("");
	const [requestHistory, setRequestHistory] = useState([]);
	const [responseLoading, setResponseLoading] = useState(false);
	const [summaryResponse, setSummaryResponse] = useState(null); // State for storing current summary
	const [invalidUrl, setInvalidUrl] = useState(false);

	// const navigate = useNavigate();

	// Fetch the user's request history when the component mounts
	useEffect(() => {
		const fetchRequests = async () => {
			try {
				const response = await api.get("/get-summary-history");

				const allRequestHistory = response.data.map(
					(summary) =>
						new SummaryResponse(
							summary.id,
							summary.url,
							summary.username,
							summary.content,
							summary.timestamp
						)
				);

				setRequestHistory(allRequestHistory);
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
			setSummaryResponse(null);
			setResponseLoading(true);
			const response = await api.post("/get-summary", { url });

			const currentSummaryResponse = new SummaryResponse(
				response.data.id,
				response.data.url,
				response.data.username,
				response.data.content,
				response.data.timestamp
			);
			setSummaryResponse(currentSummaryResponse);

			setRequestHistory((prev) => [currentSummaryResponse, ...prev]);
			console.log(setRequestHistory);

			setUrl("");
			setResponseLoading(false);
			setInvalidUrl(false);
		} catch (error) {
			setResponseLoading(false);
			console.error(error);
			if (error?.response?.status === 404) {
				setInvalidUrl(true);
			}
		}
	};

	const handleHistoryItemClick = (clickedSummaryItem) => {
		setSummaryResponse(clickedSummaryItem);
	};

	return (
		<div className="min-h-screen mx-auto p-12 bg-gray-100 flex flex-col md:flex-row">
			<Logout />
			{/* Flex container for side-by-side layout */}
			<div className="w-full md:w-1/3 bg-white p-6 rounded shadow-md mx-2 my-4">
				{" "}
				{/* All request history */}
				<div>Request History</div>
				<div className="overflow-auto p-2" style={{ maxHeight: "600px" }}>
					<ul>
						{requestHistory.map((req, ind) => (
							<SummaryHistoryItem
								active={`${summaryResponse?.id === req.id ? "true" : "false"}`}
								key={req.id}
								req={req}
								ind={ind}
								handleHistoryItemClick={handleHistoryItemClick}
							/>
						))}
					</ul>
				</div>
			</div>
			<div className="w-2/3 p-4">
				{" "}
				{/* Adjusted for form to take up remaining space */}
				<form
					onSubmit={handleRequest}
					className="bg-white p-6 rounded shadow-md "
				>
					<div>
						<h2 className="text-2xl mb-4">Submit URL</h2>
					</div>
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
						className={`w-full bg-blue-500 text-white p-2 rounded ${
							responseLoading ? "bg-zinc-700 hover:cursor-not-allowed" : ""
						}`}
						disabled={responseLoading}
					>
						{responseLoading ? "Loading..." : "Submit"}
					</button>
				</form>
				{/* Conditional Error Message */}
				<div className={`${invalidUrl ? "block" : "hidden"} text-red-500`}>
					{" "}
					{/* Step 3 */}
					Invalid URL, please enter a valid website's URL.
				</div>
				{/* Step 2: Display Area for API Response */}
				<div
					className="api-response bg-gray-200 p-4 m-6 rounded shadow-md overflow-auto"
					style={{ maxHeight: "500px" }}
				>
					{summaryResponse ? (
						<div>
							<div className="flex justify-between">
								<div className="h-3 mb-6 text-md font-bold">
									Summary for the website:{" "}
									<span className="text-lg text-blue-600">
										{" "}
										{summaryResponse.url}{" "}
									</span>
								</div>
								<div className="text-violet-700 font-semibold">
									Time: {extractTime(summaryResponse.timestamp)}
								</div>
							</div>
							<div>{summaryResponse.content}</div>
						</div>
					) : (
						"Enter a URL to get the summary"
					)}
				</div>
			</div>
		</div>
	);
};
export default Dashboard;
