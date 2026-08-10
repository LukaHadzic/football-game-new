#l/usr/bin/en bash
set -euo pipefail

COMPOSE_FILE="docker-compose.benchmark.yml"
CONTAINER="pg_benchmark"
DB_NAME="benchmark_db"
DB_DML_NAME="benchmark_db_dml"
DB_USER="benchmark_user"

echo "Starting container..."
docker compose -f "$COMPOSE_FILE" up -d

echo "Waiting for database to be ready..."
until docker exec "$CONTAINER" pg_isready -U "$DB_USER" -d "$DB_NAME" > /dev/null 2>&1;do
  sleep 1
done

echo "Ensuring dml database exists..."
docker exec -i "$CONTAINER" psql -U "$DB_USER" -d postgres -tc \
"SELECT 1 FROM pg_database WHERE datname = '$DB_DML_NAME'" | grep -q 1 || \
docker exec -i "$CONTAINER" psql -U "$DB_USER" -d postgres -c "CREATE DATABASE $DB_DML_NAME OWNER $DB_USER;"

echo "Done. Benchmark databases ready on localhost:5433"