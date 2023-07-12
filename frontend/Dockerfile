# Base image
FROM node:14-alpine

# Copy package.json and package-lock.json files
COPY package*.json ./

# Install dependencies
RUN npm install --production

# Copy remaining app files
COPY . .

# Build app for production
RUN npm run build

# Expose port 3000
EXPOSE 3000

# Start the app
CMD [ "npm", "start" ]
