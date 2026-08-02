-- Extension for UUID
CREATE EXTENSION IF NOT EXISTS pgcrypto;

SELECT setseed(0.32);
SET max_parallel_workers_per_gather = 0;

\set reference_time '2026-07-11 18:15:00';

TRUNCATE TABLE users, user_roles, email_verification_token, refresh_token
    RESTART IDENTITY CASCADE;

-- =============================
-- 1) Create table users
-- =============================
-- Distribution:
--   verified: ~85% of all users
--   created_at: exponential, only in last two years
INSERT INTO users (name, surname, email, nick, password, verified, created_at)
SELECT
    (ARRAY['Marko','Ana','Petar','Jovana','Nikola','Milica','Stefan','Ivana',
     'Luka','Teodora','Aleksandar','Jelena','Filip','Sara','Dušan'])[floor(random()*15+1)],
    (ARRAY['Jovanović','Petrović','Nikolić','Marković','Đorđević','Stojanović',
           'Ilić','Stanković','Pavlović','Milošević'])[floor(random()*10+1)],
    'user' || i || '@example.com',
    'nick_' || i,
    substr(md5(random()::text) || md5(random()::text) || md5(random()::text), 1, 60),
    random() < 0.85,
--     now() - (LEAST(730, -ln(random()) * 150) || 'days')::interval
    :'reference_time'::timestamp - (LEAST(730, -ln(random()) * 150) || 'days')::interval
FROM generate_series(1, :row_count) AS i;

-- =============================
-- 2) Create table roles
-- =============================
-- Table roles is created in init/01_shema.sql file

-- =============================
-- 3) Create table user_roles
-- =============================
-- Distribution:
--   All have ROLE_USER
--   Only 2% have ROLE_USER and ROLE_ADMIN
INSERT INTO user_roles (user_id, role_id)
SELECT id, (SELECT id FROM roles WHERE name = 'ROLE_USER')
FROM users;

INSERT INTO user_roles (user_id, role_id)
SELECT id, (SELECT id FROM roles WHERE name = 'ROLE_ADMIN')
FROM users
WHERE random()<0.2;

-- =============================
-- 4) Create table email_verification_token
-- =============================
-- Distribution:
--   All have one verification token
--   30% unused
INSERT INTO email_verification_token (token, created_at, expires_at, used, user_id)
SELECT
    gen_random_uuid()::text,
    u.created_at,
    u.created_at + interval '1day',
    CASE WHEN u.verified THEN TRUE ELSE FALSE END,
    u.id
FROM users u;

-- =============================
-- 5) Create table refresh_token
-- =============================
-- Distribution:
--   Most of users 0-3 tokens
WITH user_sessions AS (
    SELECT
        id AS user_id,
        created_at,
        GREATEST(1, 1 + floor(-ln(random()) * 2))::int AS session_count
    FROM USERS
    WHERE verified = true
),
generated AS (
    SELECT
        us.user_id,
        t.ts,
        ROW_NUMBER() OVER (PARTITION BY us.user_id ORDER BY t.ts DESC) AS rn
    FROM user_sessions us
    CROSS JOIN LATERAL generate_series(1, us.session_count) AS n
    CROSS JOIN LATERAL (
        SELECT us.created_at + (random() * (:'reference_time'::timestamp - us.created_at)) AS ts
    ) t
)
INSERT INTO refresh_token (token, revoked, created_at, expires_at, user_id)
SELECT
    gen_random_uuid()::text,
    rn <> 1,
    ts,
    ts + interval '7 day',
    user_id
FROM generated;

ANALYZE users;
ANALYZE roles;
ANALYZE user_roles;
ANALYZE email_verification_token;
ANALYZE refresh_token;

