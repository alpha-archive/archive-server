#!/bin/bash

# Archive Database & Redis Setup Script

set -e

echo "🐳 Starting PostgreSQL and Redis with Docker Compose..."

# Function to check if a command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Check prerequisites
echo "📋 Checking prerequisites..."
if ! command_exists docker; then
    echo "❌ Docker is not installed. Please install Docker first."
    exit 1
fi

if ! command_exists docker-compose; then
    echo "❌ Docker Compose is not installed. Please install Docker Compose first."
    exit 1
fi

# Check if Docker daemon is running
if ! docker info >/dev/null 2>&1; then
    echo "❌ Docker daemon is not running. Please start Docker first."
    exit 1
fi

echo "✅ Prerequisites check passed!"

# Stop and remove existing containers
echo "🛑 Stopping existing containers..."
docker-compose down --remove-orphans

# Remove old volumes (optional)
if [ "$1" = "--clean" ]; then
    echo "🧹 Cleaning up old volumes and data..."
    docker-compose down --volumes
    docker system prune -f
fi

# Start services
echo "🚀 Starting PostgreSQL and Redis..."
docker-compose up -d

# Wait for services to be healthy
echo "⏳ Waiting for services to be ready..."
echo "   - PostgreSQL..."
for i in {1..30}; do
    if docker-compose exec postgres pg_isready -U archive_user -d archive >/dev/null 2>&1; then
        break
    fi
    echo "     Waiting for PostgreSQL... ($i/30)"
    sleep 2
done

echo "   - Redis..."
for i in {1..15}; do
    if docker-compose exec redis redis-cli ping | grep -q PONG >/dev/null 2>&1; then
        break
    fi
    echo "     Waiting for Redis... ($i/15)"
    sleep 2
done

echo ""
echo "🎉 Database services are ready!"
echo ""
echo "🗄️ Database Information:"
echo "   - PostgreSQL: localhost:5432"
echo "   - Database: archive"
echo "   - Username: archive_user"
echo "   - Password: archive_password"
echo ""
echo "🔴 Redis Information:"
echo "   - Host: localhost:6379"
echo "   - Port: 6379"
echo ""
echo "🚀 Now you can start your Spring Boot application:"
echo "   ./gradlew bootRun"
echo ""
echo "📱 After starting the app, access:"
echo "   - Main Application: http://localhost:8080"
echo "   - Swagger UI: http://localhost:8080/swagger-ui.html"
echo ""
echo "📝 Useful commands:"
echo "   - View logs: docker-compose logs -f"
echo "   - Stop services: docker-compose down"
echo "   - Connect to DB: docker-compose exec postgres psql -U archive_user -d archive"
echo "   - Connect to Redis: docker-compose exec redis redis-cli"
