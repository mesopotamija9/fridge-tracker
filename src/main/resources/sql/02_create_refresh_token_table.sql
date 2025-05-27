CREATE TABLE IF NOT EXISTS public.refresh_token (
    id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    token VARCHAR(512) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_refresh_token_username
ON public.refresh_token USING BTREE (username);

CREATE INDEX IF NOT EXISTS idx_refresh_token_token
ON public.refresh_token USING BTREE (token);

CREATE INDEX IF NOT EXISTS idx_refresh_token_expires_at
ON public.refresh_token USING BTREE (expires_at);