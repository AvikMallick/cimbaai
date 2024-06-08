export const extractTime = (timestamp) => {
	const date = new Date(timestamp);

	// Extract and format individual components
	const day = date.getDate().toString().padStart(2, "0");
	const month = (date.getMonth() + 1).toString().padStart(2, "0"); // Months are 0-indexed
	const year = date.getFullYear();
	const hours = date.getHours().toString().padStart(2, "0");
	const minutes = date.getMinutes().toString().padStart(2, "0");

	const time = `${day}/${month}/${year}, ${hours}:${minutes}`;

	return time;
};
