-- name: select_all_users
EXPLAIN (ANALYZE, BUFFERS)
SELECT * FROM users;
