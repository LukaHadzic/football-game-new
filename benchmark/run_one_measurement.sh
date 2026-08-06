#!/usr/bin/env bash

set -euo pipefail

CONTAINER="pg_benchmark"
DB_NAME="benchmark_db"
DB_USER="benchmark_user"
QUERIES_FILE="benchmark/queries.sql"
RESULTS_FILE="benchmark/results/results.csv"
RUN_NUMBER="${1:-0}"
DATASET_SIZE="${2:-unknown}"
INDEX_USED="${3:-2}"

#If dir benchmark/results doesn't exist, make it
mkdir -p benchmark/results

#If .csv doesn't exist, make file with header
if [ ! -f "$RESULTS_FILE" ]; then
  echo "timestamp,dataset_size,run_number,query_name,index_used,planning_time_ms,execution_time_ms, buffers_shared_hit,buffers_shared_hit_planning,buffers_read" > "$RESULTS_FILE"
fi

#For parsing queries file
current_name=""
current_query=""

#Function for executing and gathering queries results and performance details
run_and_record() {
  local name="$1"
  local query="$2"

  [ -z "$query" ] && return #skip empty blocks

  local timestamp
  timestamp=$(date -u +%Y-%m-%dT%H:%M:%SZ)

  local output
  output=$(docker exec -i "$CONTAINER" psql -U "$DB_USER" -d "$DB_NAME" -t -A -c "$query" < /dev/null)

  local planning_time execution_time buffers_shared_hit buffers_read buffers_shared_hit_planning
  planning_time=$(echo "$output" | grep -oP 'Planning Time: \K[0-9.]+' || echo "NA")
  execution_time=$(echo "$output" | grep -oP 'Execution Time: \K[0-9.]+' || echo "NA")
  buffers_shared_hit=$(echo "$output" | grep -oP 'shared hit=\K\d+' | sed -n '1p' || echo "NA")
  buffers_shared_hit_planning=$(echo "$output" | grep -oP 'shared hit=\K\d+' | sed -n '2p' || echo "NA")
  buffers_read=$(echo "$output" | grep -oP 'read=\K\d+' || echo 0)

  echo "${timestamp},${DATASET_SIZE},${RUN_NUMBER},${name},${INDEX_USED},${planning_time},${execution_time},${buffers_shared_hit_planning},${buffers_shared_hit},${buffers_read}" >> "$RESULTS_FILE"
  echo " [$name] planning=${planning_time}ms execution=${execution_time}ms"
  echo "$output"
}

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

echo "Result saved in $RESULTS_FILE"
