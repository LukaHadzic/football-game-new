CREATE EXTENSION IF NOT EXISTS pg_prewarm;

SELECT pg_prewarm('refresh_token');
SELECT pg_prewarm('users');

-- INDEXES PREWARM...
DO $$
BEGIN
    IF EXISTS (
       SELECT 1 FROM pg_class WHERE relname = 'idx_refresh_token_user_id'
    ) THEN
       PERFORM pg_prewarm('idx_refresh_token_user_id');
   END IF;
END$$;