#!/usr/bin/env bash
set -euo pipefail

RUNS="${1:-20}"
DATASET_SIZE="${2-50000}"
CONTAINER="pg_benchmark"
DB_NAME="benchmark_db"
DB_USER="benchmark_user"


echo "Initiating $RUNS measurements on current dataset"

echo "Running VACUUM ANALYZE before measurements"
docker exec -i "$CONTAINER" psql -U "$DB_USER" -d "$DB_NAME" -c "VACUUM ANALYZE;"

echo "Warming up database cache"
docker exec -i "$CONTAINER" psql -U "$DB_USER" -d "$DB_NAME" < benchmark/cache_warmup.sql

INDEX_EXISTS=$(docker exec -i "$CONTAINER" psql -U "$DB_USER" -d "$DB_NAME" -tAc \
"SELECT CASE WHEN EXISTS (
  SELECT 1 FROM pg_class WHERE relname = 'idx_refresh_token_user_id'
  ) THEN 1 ELSE 0 END;")

for i in $(seq 1 "$RUNS"); do
  echo "--- Run $i/$RUNS for $DATASET_SIZE rows ---"
  ./benchmark/run_one_measurement.sh $i $DATASET_SIZE $INDEX_EXISTS
  sleep 2
done;

echo "All $RUNS measurements finished. Results in benchmark/results/"