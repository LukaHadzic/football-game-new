#!/usr/bin/env bash

set -euo pipefail

CONTAINER="pg_benchmark"
DB_NAME="benchmark_db_dml"
DB_USER="benchmark_user"
DATASET_SIZE="${1:-50000}"

echo "Dropping old dataset schema..."
docker exec -i "$CONTAINER" psql -U "$DB_USER" -d "$DB_NAME" -c "DROP SCHEMA public CASCADE; CREATE SCHEMA public;"
echo "Old schema dropped."

echo "Generating database with new dataset..."
docker exec -i "$CONTAINER" psql -U "$DB_USER" -d "$DB_NAME" < benchmark/sql-files/dump/baseline_"${DATASET_SIZE}".sql
echo "Done."