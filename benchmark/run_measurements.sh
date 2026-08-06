#!/usr/bin/env bash
set -euo pipefail

RUNS="${1:-20}"
DATASET_SIZE="${2-50000}"
CONTAINER="pg_benchmark"
DB_NAME="benchmark_db"
DB_USER="benchmark_user"
QUERIES_FILE="benchmark/queries.sql"
RESULTS_FILE="benchmark/results/results.csv"
RUN_NUMBER="${1:-0}"
TEST_USER_ID=$(docker exec "$CONTAINER" psql -U "$DB_USER" -d "$DB_NAME" -tAc \
"SELECT user_id FROM refresh_token GROUP BY user_id ORDER BY COUNT(*) DESC LIMIT 1 OFFSET 9")

echo "Initiating $RUNS measurements on current dataset"

echo "Running VACUUM ANALYZE before measurements"
docker exec -i "$CONTAINER" psql -U "$DB_USER" -d "$DB_NAME" -c "VACUUM ANALYZE;"

echo "Warming up database cache"
docker exec -i "$CONTAINER" psql -U "$DB_USER" -d "$DB_NAME" < benchmark/cache_warmup.sql

INDEX_USED=$(docker exec -i "$CONTAINER" psql -U "$DB_USER" -d "$DB_NAME" -tAc \
"SELECT CASE WHEN EXISTS (
  SELECT 1 FROM pg_class WHERE relname = 'idx_refresh_token_user_id'
  ) THEN 1 ELSE 0 END;")

#If dir benchmark/results doesn't exist, make it
mkdir -p benchmark/results

#If .csv doesn't exist, make file with header
if [ ! -f "$RESULTS_FILE" ]; then
  echo "timestamp,dataset_size,run_number,query_name,index_used,planning_time_ms,execution_time_ms, buffers_shared_hit,buffers_shared_hit_planning,buffers_read" > "$RESULTS_FILE"
fi

#Function for executing and gathering queries results and performance details
run_and_record() {
  local name="$1"
  local query="$2"

  [ -z "$query" ] && return #skip empty blocks

#  echo "=== Query:  $name ($RUNS runs) ==="
  for i in $(seq 1 "$RUNS"); do
    echo "=== Run $i/$RUNS for $DATASET_SIZE rows and query $name ==="

    local timestamp
    timestamp=$(date -u +%Y-%m-%dT%H:%M:%SZ)

    local output
    output=$(echo "$query" | docker exec -i "$CONTAINER" psql -U "$DB_USER" -d "$DB_NAME" \
     -v test_user_id="$TEST_USER_ID" -t -A)

    local planning_time execution_time buffers_shared_hit buffers_read buffers_shared_hit_planning
    planning_time=$(echo "$output" | grep -oP 'Planning Time: \K[0-9.]+' || echo "NA")
    execution_time=$(echo "$output" | grep -oP 'Execution Time: \K[0-9.]+' || echo "NA")
    buffers_shared_hit=$(echo "$output" | grep -oP 'shared hit=\K\d+' | sed -n '1p' || echo "NA")
    buffers_shared_hit_planning=$(echo "$output" | grep -oP 'shared hit=\K\d+' | sed -n '2p' || echo "NA")
    buffers_read=$(echo "$output" | grep -oP -m1 'read=\K\d+' || echo 0)

    echo "${timestamp},${DATASET_SIZE},${RUN_NUMBER},${name},${INDEX_USED},${planning_time},${execution_time},${buffers_shared_hit_planning},${buffers_shared_hit},${buffers_read}" >> "$RESULTS_FILE"
    echo "       [$name] planning=${planning_time}ms execution=${execution_time}ms"
    echo "       $output"
    echo ""
  done
}

#For parsing queries file
current_name=""
current_query=""

#Parse queries file
while IFS= read -r line <&3; do
  if [[ "$line" =~ ^--\ name:\ (.+)$ ]]; then
    run_and_record "$current_name" "$current_query"
    current_name="${BASH_REMATCH[1]}"
    current_query=""
  else
    current_query="${current_query} ${line}"
  fi
done 3< "$QUERIES_FILE"

#Run last query block - remains not runned after while loop
run_and_record "$current_name" "$current_query"

echo "All measurements are finished. Result saved in $RESULTS_FILE"

#for i in $(seq 1 "$RUNS"); do
#  echo "--- Run $i/$RUNS for $DATASET_SIZE rows ---"
#  ./benchmark/run_one_measurement.sh $i $DATASET_SIZE $INDEX_EXISTS
#  sleep 2
#done;
#
#echo "All $RUNS measurements finished. Results in benchmark/results/"