-- name: insert_single
INSERT INTO refresh_token (user_id, token, revoked, created_at)
VALUES (:test_user_id, 'sample_token_single_insert', false, now());

-- name: insert_bulk_1000
INSERT INTO refresh_token (user_id, token, revoked, created_at)
SELECT
    ((gs - 1) % 50000) + 1,
    'bulk_token_' || gs,
    false,
    now()
FROM generate_series(1, 1000) gs;

-- name: insert_bulk_10000
INSERT INTO refresh_token (user_id, token, revoked, created_at)
SELECT
    ((gs - 1) % 50000) + 1,
    'bulk_token_' || gs,
    false,
    now()
FROM generate_series(1, 10000) gs;

-- name: insert_bulk_100000
INSERT INTO refresh_token (user_id, token, revoked, created_at)
SELECT
    ((gs - 1) % 50000) + 1,
    'bulk_token_' || gs,
    false,
    now()
FROM generate_series(1, 100000) gs;

-- name: update_where
UPDATE refresh_token SET revoked = true
WHERE user_id = :test_user_id;

-- name: update_where_and
UPDATE refresh_token SET revoked = true
WHERE user_id = :test_user_id AND revoked = false;

-- name: update_where_and_interval
UPDATE refresh_token SET revoked = true
WHERE user_id = :test_user_id AND expires_at < now();

-- name: delete_where
DELETE FROM refresh_token
WHERE user_id = :test_user_id;

-- name: delete_where_and
DELETE FROM refresh_token
WHERE user_id = :test_user_id AND revoked = true;

-- name: delete_where_and_interval
DELETE FROM refresh_token
WHERE user_id = :test_user_id AND expires_at < now();