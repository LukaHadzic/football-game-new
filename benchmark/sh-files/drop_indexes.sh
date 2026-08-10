#!/usr/bin/env bash

set -euo pipefail

CONTAINER="pg_benchmark"
DB_NAME="benchmark_db_dml"
DB_USER="benchmark_user"

echo "Dropping index..."
docker exec -i "$CONTAINER" psql -U "$DB_USER" -d "$DB_NAME" -c "DROP INDEX IF EXISTS idx_refresh_token_user_id; ANALYZE refresh_token;"
echo "Index is dropped."