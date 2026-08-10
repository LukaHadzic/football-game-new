#!/usr/bin/env bash

set -euo pipefail

CONTAINER="pg_benchmark"
DB_NAME="benchmark_db_dml"
DB_USER="benchmark_user"
ROW_COUNT="${1:-50000}"

echo "Generating database data..."
docker exec -i "$CONTAINER" psql -U "$DB_USER" -d "$DB_NAME" -v row_count="$ROW_COUNT" < benchmark/sql-files/generate_data.sql

echo "Generating baseline ${ROW_COUNT} dump..."
docker exec "$CONTAINER" pg_dump -U "$DB_USER" "$DB_NAME" > benchmark/sql-files/dump/baseline_"${ROW_COUNT}".sql
