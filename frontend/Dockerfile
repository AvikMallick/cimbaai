# Use Node 21 as the base image
FROM node:21

# Set the working directory in the container
WORKDIR /app

# Copy package.json and package-lock.json before other files for better caching
COPY package.json package-lock.json ./

# Install dependencies
RUN npm install

# Copy the rest of the application code
COPY . .

# Build the React app
RUN npm run build

# Install serve globally to serve the application
RUN npm install -g serve

# Command to run the app using serve
CMD ["serve", "-s", "build"]