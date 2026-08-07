-- name: select_all_tokens
EXPLAIN (ANALYZE, BUFFERS)
SELECT * FROM refresh_token;
-- name: count_all_tokens
EXPLAIN (ANALYZE, BUFFERS)
SELECT COUNT(*) FROM refresh_token;

-- name: select_where
EXPLAIN (ANALYZE, BUFFERS)
SELECT * FROM refresh_token WHERE revoked = true;

-- name: select_where_order_by
EXPLAIN (ANALYZE, BUFFERS)
SELECT * FROM refresh_token WHERE revoked = true ORDER BY created_at DESC;

-- name: select_where_and
EXPLAIN (ANALYZE, BUFFERS)
SELECT * FROM refresh_token WHERE revoked = false AND user_id = :test_user_id;

-- name: select_where_and_order_by
EXPLAIN (ANALYZE, BUFFERS)
SELECT * FROM refresh_token
WHERE revoked = true AND user_id = :test_user_id
ORDER BY created_at DESC

-- name: select_where_group_by_having_order_by
EXPLAIN (ANALYZE, BUFFERS)
SELECT user_id, COUNT(*) as revoked_token_count
FROM refresh_token
WHERE revoked = true
GROUP BY USER_ID
HAVING COUNT(*) > 3
ORDER BY revoked_token_count DESC;

-- name: select_left_join
EXPLAIN (ANALYZE, BUFFERS)
SELECT u.id, u.email, rt.id as token_id, rt.revoked, rt.created_at
FROM users u
LEFT JOIN refresh_token rt ON rt.user_id = u.id;

-- name: select_left_join_and
EXPLAIN (ANALYZE, BUFFERS)
SELECT u.id, u.email, rt.id as token_id, rt.created_at
FROM users u
LEFT JOIN refresh_token rt ON rt.user_id = u.id AND rt.revoked = true;

-- name: select_full_outer_join
EXPLAIN (ANALYZE, BUFFERS)
SELECT *
FROM users u
FULL OUTER JOIN refresh_token rt ON rt.user_id = u.id;
