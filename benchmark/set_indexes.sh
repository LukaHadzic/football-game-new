#!/usr/bin/env bash

set -euo pipefail

CONTAINER="pg_benchmark"
DB_NAME="benchmark_db"
DB_USER="benchmark_user"

echo "Setting index..."
docker exec -i "$CONTAINER" psql -U "$DB_USER" -d "$DB_NAME" -c "CREATE INDEX IF NOT EXISTS idx_refresh_token_user_id ON refresh_token(user_id); ANALYZE refresh_token;"
echo "Index is set."