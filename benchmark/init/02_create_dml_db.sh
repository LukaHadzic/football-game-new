#!/usr/bin/env bash
set -e

DB_NAME=$"benchmark_db_dml"
DB_USER=$"benchmark_user"

psql -v ON_ERROR_STOP=1 --username "$DB_USER" --dbname postgres <<-EOSQL
  CREATE DATABASE $DB_NAME OWNER $DB_USER;
EOSQL