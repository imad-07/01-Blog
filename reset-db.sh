#!/bin/bash

# Database Reset Script
# This will stop all containers, remove volumes (wiping the database), and restart the stack.

echo "Stopping containers and removing volumes..."
docker-compose down -v

echo "Rebuilding and starting services..."
docker-compose up --build -d

echo "Database reset complete. The application is starting up."
echo "Wait a few seconds for the backend to initialize the admin account."
