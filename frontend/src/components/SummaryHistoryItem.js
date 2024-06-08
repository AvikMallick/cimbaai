const SummaryHistoryItem = ({ req, ind, active, handleHistoryItemClick }) => {
	const handleClick = () => {
		handleHistoryItemClick(req);
	};

	return (
		<li
			className={`p-2 mb-1 hover:cursor-pointer rounded-md overflow-x-clip ${
				active === "true" ? "outline outline-4 outline-blue-500 " : ""
			}
							${ind % 2 === 0 ? "bg-gray-300" : "bg-gray-400"}`}
			onClick={handleClick}
		>
			<button className="flex flex-row">
				<span className="px-2">{ind + 1}</span>{" "}
				<span className="px-2">{req.url}</span>
			</button>
		</li>
	);
};
export default SummaryHistoryItem;
