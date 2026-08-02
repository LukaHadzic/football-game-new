#l/usr/bin/en bash
set -euo pipefail

COMPOSE_FILE="docker-compose.benchmark.yml"
CONTAINER="pg_benchmark"
DB_NAME="benchmark_db"
DB_USER="benchmark_user"

echo "Starting container..."
docker compose -f "$COMPOSE_FILE" up -d

echo "Waiting for database to be ready..."
until docker exec "$CONTAINER" pg_isready -U "$DB_USER" -d "$DB_NAME" > /dev/null 2>&1;do
  sleep 1
done

echo "Done. Benchmark database ready on localhost:5433"